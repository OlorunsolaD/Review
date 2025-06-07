// src/main/java/com/reviewyme/userservice/service/UserService.java
package com.reviewyme.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewyme.encryption.EncryptionService;
import com.reviewyme.hashing.HashingService;

import com.reviewyme.userservice.model.ExpertUserMapping;
import com.reviewyme.userservice.model.User;
import com.reviewyme.userservice.model.UserDetails;
import com.reviewyme.userservice.repository.ExpertUserMappingRepository;
import com.reviewyme.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.reviewyme.userservice.dto.Role.*;

@Service
public class UserService {

    private final HashingService hashingService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final ExpertUserMappingRepository expertUserMappingRepository;
    private final ObjectMapper objectMapper;

    public UserService(HashingService hashingService,
                       EncryptionService encryptionService,
                       UserRepository userRepository,
                       ExpertUserMappingRepository expertUserMappingRepository,
                       ObjectMapper objectMapper) {
        this.hashingService = hashingService;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.expertUserMappingRepository = expertUserMappingRepository;
        this.objectMapper = objectMapper;
    }

    private User createNewUser(String email, String rawPassword, UserDetails userDetails, Set<String> roles) throws JsonProcessingException {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String hashedPassword = hashingService.hash(rawPassword);

        String userDetailsJson = objectMapper.writeValueAsString(
                UserDetails.builder()
                        .firstName(userDetails.getFirstName())
                        .lastName(userDetails.getLastName())
                        .address(userDetails.getAddress())
                        .isExpert(userDetails.isExpert())
                        .build());

        String encryptedDetails = encryptionService.encrypt(userDetailsJson);

        User newUser = User.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .encryptedUserDetails(encryptedDetails)
                .roles(roles)
                .build();

        return userRepository.save(newUser);
    }

    public User registerUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        userDetails.setExpert(false);
        return createNewUser(email, rawPassword, userDetails, Collections.singleton("ROLE_USER"));
    }

    public User registerAdminUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add(String.valueOf(ROLE_USER));
        adminRoles.add(String.valueOf(ROLE_ADMIN));
        // Admins are not "experts" in this context, so explicitly set isExpert to false.
        userDetails.setExpert(false);
        return createNewUser(email, rawPassword, userDetails, adminRoles);
    }

    public User registerInternalServiceUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> internalServiceRoles = new HashSet<>();
        internalServiceRoles.add(String.valueOf(ROLE_INTERNAL_SERVICE));
        return createNewUser(email, rawPassword, userDetails, internalServiceRoles);
    }

    // This method is called by admins to create users who are 'experts'.
    public User registerExpertUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        // The `userDetails` object passed here will have the `isExpert` flag
        // as determined by the admin's request. We pass it directly to createNewUser.
        Set<String> expertRoles = Collections.singleton("ROLE_USER"); // Experts still have ROLE_USER
        userDetails.setExpert(true); // Ensure the userDetails reflects that this is an expert user
        User user = createNewUser(email, rawPassword, userDetails, expertRoles);

        // NEW: Create an ExpertUserMapping for this expert
        ExpertUserMapping expertMapping = ExpertUserMapping.builder()
                .userId(user.getId())
                .completedResumeIds(Collections.emptyList())
                .assignedResumeIds(Collections.emptyList())
                .build();

        expertUserMappingRepository.save(expertMapping);

        return user;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    private Optional<UserDetails> mapUserToUserDetails(User user) throws JsonProcessingException {
        String encryptedDetails = user.getEncryptedUserDetails();
        String decryptedDetailsJson = encryptionService.decrypt(encryptedDetails);

        UserDetails userDetailsFromEncrypted = objectMapper.readValue(decryptedDetailsJson, UserDetails.class);

        UserDetails userDetailsWithId = UserDetails.builder()
                .id(user.getId())
                .firstName(userDetailsFromEncrypted.getFirstName())
                .lastName(userDetailsFromEncrypted.getLastName())
                .address(userDetailsFromEncrypted.getAddress())
                .isExpert(userDetailsFromEncrypted.isExpert())
                .build();

        return Optional.of(userDetailsWithId);
    }

    // Refactored using the new helper method
    public Optional<UserDetails> getUserDetailsByEmail(String email) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userOptional.isPresent() ? mapUserToUserDetails(userOptional.get()) : Optional.empty();
    }

    // Refactored using the new helper method
    public Optional<UserDetails> getUserDetailsById(String userId) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.isPresent() ? mapUserToUserDetails(userOptional.get()) : Optional.empty();
    }
}