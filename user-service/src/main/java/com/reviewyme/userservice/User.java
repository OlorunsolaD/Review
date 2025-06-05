package com.reviewyme.userservice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String email;
    @JsonIgnore
    private String passwordHash;
    private String encryptedUserDetails;
    @Builder.Default // Initialize roles set by default
    private Set<String> roles = new HashSet<>(); // e.g., "ROLE_USER", "ROLE_ADMIN"
}