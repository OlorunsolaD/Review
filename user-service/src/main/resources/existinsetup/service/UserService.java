package com.reviewyme.userservice.existinsetup.service;

import com.reviewyme.userservice.existinsetup.dto.UserRegistrationRequest;
import com.reviewyme.userservice.existinsetup.exception.UserNotFoundException;
import com.reviewyme.userservice.existinsetup.model.UserEntity;
import com.reviewyme.userservice.existinsetup.model.UserStatus;


public interface UserService {
    UserEntity createUser(UserRegistrationRequest userRegistrationRequest);

    UserRegistrationRequest findByUserId(String userId);

    UserEntity findByEmailAndPassword (String email, String rawPassword) throws UserNotFoundException;

    UserEntity updateUser(String id, UserRegistrationRequest userRegistrationRequest);

    UserRegistrationRequest updateUserStatus(String id, UserStatus status) throws UserNotFoundException;

    UserRegistrationRequest findUserById (String id) throws UserNotFoundException;

    UserEntity findUserStatusById (String id);

}
