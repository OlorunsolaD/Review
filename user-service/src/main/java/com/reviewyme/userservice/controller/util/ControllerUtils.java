// src/main/java/com/reviewyme/userservice/controller/util/ControllerUtils.java
package com.reviewyme.userservice.controller.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reviewyme.userservice.model.User;
import com.reviewyme.userservice.model.UserDetails;
import com.reviewyme.userservice.dto.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ControllerUtils {

    // Custom functional interface for a method with three arguments and a return value
    @FunctionalInterface
    public interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3) throws JsonProcessingException;
    }

    public static ResponseEntity<?> handleRegistrationRequest(
            SignupRequest request,
            TriFunction<String, String, UserDetails, User> registrationFunction,
            String successMessagePrefix) {

        try {
            UserDetails userDetails = UserDetails.builder()
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .address(request.address())
                    .build();

            User registeredUser = registrationFunction.apply(request.email(), request.password(), userDetails);

            return ResponseEntity.status(HttpStatus.CREATED).body(successMessagePrefix + " registered successfully with ID: " + registeredUser.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(successMessagePrefix + " registration failed: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing user details: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(successMessagePrefix + " registration failed: " + e.getMessage());
        }
    }
}