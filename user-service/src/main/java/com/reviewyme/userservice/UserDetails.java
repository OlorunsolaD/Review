package com.reviewyme.userservice;

import lombok.Builder;

// UserDetails is now a record. Lombok's @Data, @NoArgsConstructor, @AllArgsConstructor are implied.
// We keep @Builder for easy instantiation.
@Builder // Provides a builder for easy object creation
public record UserDetails(
        String firstName,
        String lastName,
        String address
) {
}