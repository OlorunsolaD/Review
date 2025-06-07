// src/main/java/com/reviewyme/userservice/service/UserService.java
package com.reviewyme.userservice.service.just;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewyme.encryption.EncryptionService;
import com.reviewyme.hashing.HashingService;

import com.reviewyme.userservice.dto.UserProfileResponseDTO;
import com.reviewyme.userservice.model.ExpertUserMapping;
import com.reviewyme.userservice.model.User;
import com.reviewyme.userservice.model.UserDetails;
import com.reviewyme.userservice.repository.ExpertUserMappingRepository;
import com.reviewyme.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceNew {

    private final HashingService hashingService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final ExpertUserMappingRepository expertUserMappingRepository; // Inject new repository
    private final ObjectMapper objectMapper;

    public UserServiceNew(HashingService hashingService,
                          EncryptionService encryptionService,
                          UserRepository userRepository,
                          ExpertUserMappingRepository expertUserMappingRepository, // Add to constructor
                          ObjectMapper objectMapper) {
        this.hashingService = hashingService;
        this.encryptionService = encryptionService;
        this.userRepository = userRepository;
        this.expertUserMappingRepository = expertUserMappingRepository; // Assign
        this.objectMapper = objectMapper;
    }

    // UPDATED: createNewUser - REMOVED encryptedExpertSpecifics
    private User createNewUser(String email, String rawPassword, UserDetails userDetails, Set<String> roles) throws JsonProcessingException {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String hashedPassword = hashingService.hash(rawPassword);

        String userDetailsJson = objectMapper.writeValueAsString(
            new UserDetails(
                null,
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAddress(),
                userDetails.isExpert()
            )
        );
        String encryptedDetails = encryptionService.encrypt(userDetailsJson);

        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .passwordHash(hashedPassword)
                .encryptedUserDetails(encryptedDetails)
                // .encryptedExpertSpecifics(encryptedExpertSpecifics) // REMOVED
                .roles(roles)
                .build();

        return userRepository.save(newUser);
    }

    // ... (registerUser, registerAdminUser, registerInternalServiceUser remain largely the same,
    //      but ensure they create User with expert=false which leads to no ExpertUserMapping creation) ...
    public User registerUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        UserDetails regularUserDetails = new UserDetails(
            userDetails.getId(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getAddress(), false
        );
        return createNewUser(email, rawPassword, regularUserDetails, Collections.singleton("ROLE_USER"));
    }

    public User registerAdminUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("ROLE_USER");
        adminRoles.add("ROLE_ADMIN");
        UserDetails adminUserDetails = new UserDetails(
            userDetails.getId(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getAddress(), false
        );
        return createNewUser(email, rawPassword, adminUserDetails, adminRoles);
    }

    public User registerInternalServiceUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> internalServiceRoles = new HashSet<>();
        internalServiceRoles.add("ROLE_INTERNAL_SERVICE");
        UserDetails internalUserDetails = new UserDetails(
            userDetails.getId(), userDetails.getFirstName(), userDetails.getLastName(), userDetails.getAddress(), false
        );
        return createNewUser(email, rawPassword, internalUserDetails, internalServiceRoles);
    }


    // UPDATED: registerExpertUser - now creates ExpertUserMapping
    public User registerExpertUser(String email, String rawPassword, UserDetails userDetails) throws JsonProcessingException {
        Set<String> expertRoles = Collections.singleton("ROLE_USER");
        User user = createNewUser(email, rawPassword, userDetails, expertRoles); // User is created with isExpert = true

        // NEW: Create an ExpertUserMapping for this expert
        ExpertUserMapping expertMapping = ExpertUserMapping.builder()
                                        .userId(user.getId())
                                        .completedResumeIds(Collections.emptyList())
                                        .assignedResumeIds(Collections.emptyList())
                                        .build();
        expertUserMappingRepository.save(expertMapping); // Save the mapping

        return user;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    // UPDATED: mapUserToUserProfileDTO - now fetches ExpertUserMapping if applicable
    private Optional<UserProfileResponseDTO> mapUserToUserProfileDTO(User user) throws JsonProcessingException {
        String decryptedDetailsJson = encryptionService.decrypt(user.getEncryptedUserDetails());
        UserDetails userDetails = objectMapper.readValue(decryptedDetailsJson, UserDetails.class);

        UserProfileResponseDTO.UserProfileResponseDTOBuilder dtoBuilder = UserProfileResponseDTO.builder()
            .id(user.getId())
            .firstName(userDetails.getFirstName())
            .lastName(userDetails.getLastName())
            .address(userDetails.getAddress())
            .isExpert(userDetails.isExpert());

        // Conditionally fetch ExpertUserMapping details
        if (userDetails.isExpert()) {
            Optional<ExpertUserMapping> expertMapping = expertUserMappingRepository.findByUserId(user.getId());
            if (expertMapping.isPresent()) {
                dtoBuilder
                    .completedResumeIds(expertMapping.get().getCompletedResumeIds())
                    .assignedResumeIds(expertMapping.get().getAssignedResumeIds());
            } else {
                // If expert flag is true but no mapping found (e.g., data inconsistency), default to empty lists
                dtoBuilder
                    .completedResumeIds(Collections.emptyList())
                    .assignedResumeIds(Collections.emptyList());
            }
        } else {
            // For non-experts, ensure lists are empty
            dtoBuilder
                .completedResumeIds(Collections.emptyList())
                .assignedResumeIds(Collections.emptyList());
        }

        return Optional.of(dtoBuilder.build());
    }

    // Existing methods, but now return UserProfileResponseDTO
    public Optional<UserProfileResponseDTO> getUserDetailsByEmail(String email) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        return userOptional.isPresent() ? mapUserToUserProfileDTO(userOptional.get()) : Optional.empty();
    }

    public Optional<UserProfileResponseDTO> getUserDetailsById(String userId) throws JsonProcessingException {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.isPresent() ? mapUserToUserProfileDTO(userOptional.get()) : Optional.empty();
    }

    // NEW: Method to find experts with the least assigned resumes
    // This query will now be efficient because it operates on the unencrypted ExpertUserMapping collection
    public List<UserProfileResponseDTO> findExpertsWithLeastAssignedResumes(int limit) throws JsonProcessingException {
        // This assumes you add a field like 'assignedCount' to ExpertUserMapping and index it.
        // For example, if ExpertUserMapping has `private int assignedCount;`:
        // List<ExpertUserMapping> expertMappings = expertUserMappingRepository.findByOrderByAssignedCountAsc(PageRequest.of(0, limit));
        //
        // If you're sorting by the size of the `assignedResumeIds` list directly in MongoDB,
        // you'd typically use an aggregation pipeline.
        // For simplicity, let's assume we fetch all and sort in memory if the list is small,
        // or add a dedicated `assignedCount` field. For 4 million records, you NEED a pre-calculated count.

        // Placeholder: Fetch all and sort (NOT EFFICIENT FOR LARGE DATA)
        List<ExpertUserMapping> allExpertMappings = expertUserMappingRepository.findAll();

        // Sort by assignedResumeIds list size (in-memory sort for demonstration, optimize with DB-level sort)
        allExpertMappings.sort((e1, e2) -> Integer.compare(e1.getAssignedResumeIds().size(), e2.getAssignedResumeIds().size()));

        List<UserProfileResponseDTO> experts = new ArrayList<>();
        int count = 0;
        for (ExpertUserMapping mapping : allExpertMappings) {
            if (count >= limit) break;
            Optional<User> userOptional = userRepository.findById(mapping.getUserId());
            if (userOptional.isPresent()) {
                experts.add(mapUserToUserProfileDTO(userOptional.get()).get());
                count++;
            }
        }
        return experts;

        // Ideal approach for large data:
        // 1. Add `private int assignedCount;` to ExpertUserMapping.
        // 2. Update `assignedCount` whenever `assignedResumeIds` changes.
        // 3. Create index on `assignedCount`.
        // 4. Then your repository method would be:
        //    List<ExpertUserMapping> topExperts = expertUserMappingRepository.findTopXByOrderByAssignedCountAsc(Pageable pageable);
        //    (where X is your limit)
        // 5. Map these topExperts to UserProfileResponseDTOs by fetching corresponding User records.
    }

    // New method to update assigned/completed resumes (example)
    public Optional<ExpertUserMapping> updateExpertResumeStatus(String userId, List<String> assignedIds, List<String> completedIds) {
        Optional<ExpertUserMapping> expertMappingOpt = expertUserMappingRepository.findByUserId(userId);
        if (expertMappingOpt.isPresent()) {
            ExpertUserMapping mapping = expertMappingOpt.get();
            mapping.setAssignedResumeIds(assignedIds);
            mapping.setCompletedResumeIds(completedIds);
            // If you had an `assignedCount` field, update it here:
            // mapping.setAssignedCount(assignedIds.size());
            return Optional.of(expertUserMappingRepository.save(mapping));
        }
        return Optional.empty();
    }
}