package com.reviewyme.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User {
    @Id
    private String id;

    private String email;
    @JsonIgnore
    private String passwordHash;
    private String encryptedUserDetails;
    @Builder.Default
    private Set<String> roles = new HashSet<>();
}