package com.reviewyme.userservice.existinsetup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
//    private String id;
    private String userId;
    private String fullName;
    private String email;
}
