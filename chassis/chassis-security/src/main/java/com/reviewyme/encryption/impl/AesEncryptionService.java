package com.reviewyme.encryption.impl;

import com.reviewyme.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AesEncryptionService implements EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String MODE = "GCM";
    private static final String PADDING = "NoPadding";
    private static final String TRANSFORMATION = ALGORITHM + "/" + MODE + "/" + PADDING;
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits

    private final SecretKey secretKey;

    public AesEncryptionService(@Value("${security.encryption.secret-key}") String base64SecretKey) {
        if (base64SecretKey == null || base64SecretKey.isEmpty()) {
            throw new IllegalArgumentException("Encryption secret key must be provided in application.yml or as an environment variable.");
        }
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        if (keyBytes.length != 32) { // 256 bits for AES-256
            throw new IllegalArgumentException("Encryption secret key must be 32 bytes (256 bits) after Base64 decoding.");
        }
        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    @Override
    public String encrypt(String data) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv); // Generate a new IV for each encryption

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes());

            // Prepend IV to the ciphertext for decryption
            byte[] encryptedWithIv = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedWithIv, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decodedBytes, 0, iv, 0, GCM_IV_LENGTH);

            byte[] ciphertext = new byte[decodedBytes.length - GCM_IV_LENGTH];
            System.arraycopy(decodedBytes, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(ciphertext);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data. Possible tampering or incorrect key/IV.", e);
        }
    }
}