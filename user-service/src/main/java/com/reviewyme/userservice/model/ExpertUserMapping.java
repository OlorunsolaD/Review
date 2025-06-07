// src/main/java/com/reviewyme/model/ExpertUserMapping.java
package com.reviewyme.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList; // Use ArrayList for mutable lists
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "expert_user_mappings") // This maps to a new MongoDB collection
public class ExpertUserMapping {
    @Id
    private String userId; // This ID will be the same as the User's ID
    private List<String> completedResumeIds = new ArrayList<>();
    private List<String> assignedResumeIds = new ArrayList<>();

    // You can add more fields here that are specific to experts and need to be queryable
     private int assignedCount; // You could add a direct count for easier querying/sorting
}