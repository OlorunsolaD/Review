package com.reviewyme.userservice.existinsetup.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


@Data
@Builder
@Document(collection = "registration")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private String id;

    private String userId; // unique userId
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phoneNumber;

    private Set<UserRole> roles; //To store multiple roles such as user and admin
    private UserStatus Status;

//    private String resumeId;


}