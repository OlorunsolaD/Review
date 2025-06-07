package com.reviewyme.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Standard JWT token type

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}