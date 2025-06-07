package com.reviewyme.userservice.dto;

import lombok.Builder;

// LoginRequest is now a record. Lombok's @Data, @NoArgsConstructor, @AllArgsConstructor are implied.
// We keep @Builder for easy instantiation if needed in tests or elsewhere.
@Builder
public record LoginRequest(
        String email,
        String password
) {
}