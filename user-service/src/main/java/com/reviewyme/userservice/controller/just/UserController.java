// src/main/java/com/reviewyme/userservice/controller/UserController.java
package com.reviewyme.userservice.controller.just;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reviewyme.userservice.auth.JwtTokenProvider;
import com.reviewyme.userservice.dto.JwtAuthResponse;
import com.reviewyme.userservice.dto.LoginRequest;
import com.reviewyme.userservice.dto.SignupRequest;
import com.reviewyme.userservice.dto.UserProfileResponseDTO;
import com.reviewyme.userservice.model.User;
import com.reviewyme.userservice.service.just.UserServiceNew;
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
import java.util.List; // Added for list
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceNew userServiceNew;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserServiceNew userServiceNew,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.userServiceNew = userServiceNew;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @FunctionalInterface
    interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3) throws JsonProcessingException;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userServiceNew::registerUser,
                "User"
        );
    }

    @PostMapping("/admin/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupAdmin(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userServiceNew::registerAdminUser,
                "Admin user"
        );
    }

    @PostMapping("/internal-service/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupInternalServiceUser(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userServiceNew::registerInternalServiceUser,
                "Internal service user"
        );
    }

    @PostMapping("/expert/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> signupExpert(@RequestBody SignupRequest request) {
        return handleRegistrationRequest(
                request,
                userServiceNew::registerExpertUser,
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
            Optional<UserProfileResponseDTO> userProfile = userServiceNew.getUserDetailsByEmail(email);
            if (userProfile.isPresent()) {
                return ResponseEntity.ok(userProfile.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user profile: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'INTERNAL_SERVICE')")
    public ResponseEntity<?> getUserById(@PathVariable String id, Principal principal) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            boolean hasAdminOrInternalRole = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_INTERNAL_SERVICE"));

            if (!hasAdminOrInternalRole) {
                String authenticatedUserEmail = principal.getName();
                Optional<User> authenticatedUser = userServiceNew.findUserByEmail(authenticatedUserEmail);

                if (authenticatedUser.isEmpty() || !authenticatedUser.get().getId().equals(id)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Access Denied: As a regular user, you can only view your own profile.");
                }
            }

            Optional<UserProfileResponseDTO> userProfile = userServiceNew.getUserDetailsById(id);

            if (userProfile.isPresent()) {
                return ResponseEntity.ok(userProfile.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found.");
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user details: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // NEW: Endpoint to get experts with the least assigned resumes
    @GetMapping("/experts/least-assigned")
    @PreAuthorize("hasRole('ADMIN') or hasRole('INTERNAL_SERVICE')") // Only Admins or Internal Services can request this
    public ResponseEntity<?> getExpertsWithLeastAssignedResumes(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserProfileResponseDTO> experts = userServiceNew.findExpertsWithLeastAssignedResumes(limit);
            return ResponseEntity.ok(experts);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving expert list: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}