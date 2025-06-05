// src/main/java/com/reviewyme/userservice/UserService.java
package com.reviewyme.userservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewyme.encryption.EncryptionService; // Ensure correct package for EncryptionService
import com.reviewyme.hashing.HashingService;       // Ensure correct package for HashingService
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final HashingService hashingService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // Let Spring inject these dependencies
    public UserService(HashingService hashingService,       // Spring will inject BcryptHashingService
                       EncryptionService encryptionService, // Spring will inject AesEncryptionService
                       UserRepository userRepository,
                       ObjectMapper objectMapper) {
        this.hashingService = hashingService;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public User registerUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String hashedPassword = hashingService.hash(rawPassword);
        String userDetailsJson = objectMapper.writeValueAsString(userDetails);
        String encryptedDetails = encryptionService.encrypt(userDetailsJson);

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .passwordHash(hashedPassword)
                .encryptedUserDetails(encryptedDetails)
                .roles(Collections.singleton(Role.ROLE_USER.toString()))
                .build();

        return userRepository.save(newUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<UserDetails> getUserDetails(String email) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encryptedDetails = user.getEncryptedUserDetails(); // Corrected access for record
            String decryptedDetailsJson = encryptionService.decrypt(encryptedDetails);
            return Optional.of(objectMapper.readValue(decryptedDetailsJson, UserDetails.class));
        }
        return Optional.empty();
    }
}