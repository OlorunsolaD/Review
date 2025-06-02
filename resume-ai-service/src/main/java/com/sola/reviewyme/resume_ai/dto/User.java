package com.sola.reviewyme.resume_ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class User {

//    private String id;
    @JsonProperty("userId")
    private String userId;

//    @NotBlank(message = "Full name is required")
    private String fullName;

//    @NotBlank(message = "Email must not be blank")
//    @Email(message = "Email should be valid")
    private String email;
}
