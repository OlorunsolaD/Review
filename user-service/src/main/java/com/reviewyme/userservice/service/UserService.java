// src/main/java/com/reviewyme/userservice/service/UserService.java
package com.reviewyme.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewyme.encryption.EncryptionService;
import com.reviewyme.hashing.HashingService;

import com.reviewyme.userservice.model.User;
import com.reviewyme.userservice.model.UserDetails;
import com.reviewyme.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final HashingService hashingService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(HashingService hashingService,
                       EncryptionService encryptionService,
                       UserRepository userRepository,
                       ObjectMapper objectMapper) {
        this.hashingService = hashingService;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // Private helper method to handle common user creation logic
    private User createNewUser(String email, String rawPassword, UserDetails userDetails, Set<String> roles) throws JsonProcessingException {
        // Check if user already exists
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        // Hash password
        String hashedPassword = hashingService.hash(rawPassword);

        // Serialize and encrypt user details
        String userDetailsJson = objectMapper.writeValueAsString(userDetails);
        String encryptedDetails = encryptionService.encrypt(userDetailsJson);

        // Build and save the new user
        User newUser = User.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .encryptedUserDetails(encryptedDetails)
                .roles(roles) // Assign the provided roles
                .build();

        return userRepository.save(newUser);
    }

    public User registerUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        return createNewUser(email, rawPassword, userDetails, Collections.singleton("ROLE_USER"));
    }

    public User registerAdminUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("ROLE_USER");
        adminRoles.add("ROLE_ADMIN");
        return createNewUser(email, rawPassword, userDetails, adminRoles);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<UserDetails> getUserDetails(String email) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encryptedDetails = user.getEncryptedUserDetails(); // Access record component directly
            String decryptedDetailsJson = encryptionService.decrypt(encryptedDetails);

            // Read the decrypted JSON into a UserDetails object
            UserDetails userDetailsFromEncrypted = objectMapper.readValue(decryptedDetailsJson, UserDetails.class);

            UserDetails userDetailsWithId = new UserDetails(
                    user.getId(), // Set the ID from the User record
                    userDetailsFromEncrypted.getFirstName(),
                    userDetailsFromEncrypted.getLastName(),
                    userDetailsFromEncrypted.getAddress()
            );

            return Optional.of(userDetailsWithId);
        }
        return Optional.empty();
    }
}