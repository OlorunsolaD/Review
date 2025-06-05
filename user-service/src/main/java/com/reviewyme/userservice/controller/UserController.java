// src/main/java/com/reviewyme/userservice/controller/UserController.java
package com.reviewyme.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reviewyme.userservice.auth.JwtTokenProvider;
import com.reviewyme.userservice.dto.JwtAuthResponse;
import com.reviewyme.userservice.dto.LoginRequest;
import com.reviewyme.userservice.dto.SignupRequest;
import com.reviewyme.userservice.model.UserDetails;
import com.reviewyme.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.reviewyme.userservice.controller.util.ControllerUtils.handleRegistrationRequest;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup") // Existing endpoint for regular user signup
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userService::registerUser, // Pass method reference to the common handler
                "User"
        );
    }

    @PostMapping("/admin/signup") // New endpoint for Admin user signup - PROTECTED!
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupAdmin(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userService::registerAdminUser, // Pass method reference to the common handler
                "Admin user"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthResponse(token));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        try {
            String email = principal.getName();
            Optional<UserDetails> userDetails = userService.getUserDetails(email);
            if (userDetails.isPresent()) {
                return ResponseEntity.ok(userDetails.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user profile: " + e.getMessage());
        }
    }

    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard!");
    }
}