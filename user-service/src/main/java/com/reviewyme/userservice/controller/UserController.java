// src/main/java/com/reviewyme/userservice/controller/UserController.java
package com.reviewyme.userservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reviewyme.userservice.auth.JwtTokenProvider;
import com.reviewyme.userservice.dto.JwtAuthResponse;
import com.reviewyme.userservice.dto.LoginRequest;
import com.reviewyme.userservice.dto.SignupRequest;
import com.reviewyme.userservice.model.User;
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

//@RestController
//@RequestMapping("/api/users")
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
                userService::registerUser,
                "User"
        );
    }

    @PostMapping("/admin/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupAdmin(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userService::registerAdminUser,
                "Admin user"
        );
    }

    @PostMapping("/internal-service/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupInternalServiceUser(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userService::registerInternalServiceUser,
                "Internal service user"
        );
    }

    // NEW: Endpoint to register expert users (ROLE_USER with isExpert=true/false as per request)
    @PostMapping("/expert/signup")
    @PreAuthorize("hasRole('ADMIN')") // Only ADMINs can create expert users
    public ResponseEntity<?> signupExpert(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userService::registerExpertUser, // Call the new UserService method
                "Expert user"
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
            Optional<UserDetails> userDetails = userService.getUserDetailsByEmail(email);
            if (userDetails.isPresent()) {
                return ResponseEntity.ok(userDetails.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user profile: " + e.getMessage());
        }
    }

    // --- UPDATED: Endpoint to get UserDetails by ID for multiple roles ---
    @GetMapping("/{id}")
    // This PreAuthorize ensures that only authenticated users with at least one of these roles can attempt access.
    // The fine-grained logic happens inside the method.
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'INTERNAL_SERVICE')")
    public ResponseEntity<?> getUserById(@PathVariable String id, Principal principal) {
        try {
            // Get the current authentication object for roles
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if the authenticated user is an ADMIN or INTERNAL_SERVICE
            boolean hasAdminOrInternalRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_INTERNAL_SERVICE"));

            // If the user does NOT have ADMIN or INTERNAL_SERVICE role,
            // then it must be a regular USER, and we must check if they are requesting their own profile.
            if (!hasAdminOrInternalRole) {
                // This user must be a ROLE_USER attempting to view their own profile.
                String authenticatedUserEmail = principal.getName();
                Optional<User> authenticatedUser = userService.findUserByEmail(authenticatedUserEmail);

                // Deny access if:
                // 1. The authenticated user's data isn't found (shouldn't happen if authenticated).
                // 2. The authenticated user's ID does NOT match the requested ID in the path variable.
                if (authenticatedUser.isEmpty() || !authenticatedUser.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Access Denied: As a regular user, you can only view your own profile.");
                }
                // If they passed this check, it means they are ROLE_USER and requesting their own ID.
            }

            // At this point, the request is authorized (either admin/internal service OR regular user requesting self).
            Optional<UserDetails> userDetails = userService.getUserDetailsById(id);

            if (userDetails.isPresent()) {
                return ResponseEntity.ok(userDetails.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user details: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard!");
    }
}