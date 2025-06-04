package com.reviewyme.hashing.impl;

import com.reviewyme.hashing.HashingService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptHashingService implements HashingService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BcryptHashingService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public String hash(String data) {
        return passwordEncoder.encode(data);
    }

    @Override
    public boolean matches(String rawData, String hashedData) {
        return passwordEncoder.matches(rawData, hashedData);
    }
}