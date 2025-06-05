package com.reviewyme.userservice;

import lombok.Builder;

// SignupRequest is now a record.
@Builder
public record SignupRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String address
) {
}