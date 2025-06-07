// src/main/java/com/reviewyme/userservice/controller/UserProfileResponseDTO.java
package com.reviewyme.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponseDTO {
    // Fields from UserDetails
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    @JsonProperty("isExpert")
    private boolean isExpert;

    // Fields from ExpertSpecifics (now fetched from ExpertUserMapping)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> completedResumeIds;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> assignedResumeIds;
}