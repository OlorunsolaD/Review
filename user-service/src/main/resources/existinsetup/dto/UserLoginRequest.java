package com.reviewyme.userservice.existinsetup.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {

    @NotBlank(message = "Email is Required")
    @Email(message = "Email should be Valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
