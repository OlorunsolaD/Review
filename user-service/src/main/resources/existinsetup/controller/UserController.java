package com.reviewyme.userservice.existinsetup.controller;
import com.reviewyme.userservice.existinsetup.dto.LoginResponseDto;
import com.reviewyme.userservice.existinsetup.dto.UserLoginRequest;
import com.reviewyme.userservice.existinsetup.dto.UserRegistrationRequest;
import com.reviewyme.userservice.existinsetup.dto.UserResponseDto;
import com.reviewyme.userservice.existinsetup.model.UserEntity;
import com.reviewyme.userservice.existinsetup.model.UserStatus;
import com.reviewyme.userservice.existinsetup.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User service", description = "Operations for User Management")
public class UserController {

//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public UserController(UserService userService,
//                          PasswordEncoder passwordEncoder) {
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // Public - No Authentication Required!
//    @PostMapping ("/register")
//    public ResponseEntity<UserResponseDto> registerUser(
//            @Parameter(description = "User Registration Request", required = true)
//            @Valid @RequestPart("userRegistrationRequest") @RequestBody UserRegistrationRequest userRegistrationRequest) {
//        // Create user
//        UserEntity createdUser = userService.createUser(userRegistrationRequest);
//
//
//        // Map the created user to a UserResponseDto and return it as the response
//        UserResponseDto userResponseDto = new UserResponseDto(
////                createdUser.getId(),
//                createdUser.getUserId(),
//                createdUser.getFullName(),
//                createdUser.getEmail()
//        );
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<UserRegistrationRequest> findUserId (@PathVariable String userId){
//        UserRegistrationRequest user= userService.findByUserId(userId);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(user);
//
//    }
//
//    // Public - No Authentication Required!
//    @GetMapping("/find/{id}")
//    public ResponseEntity<UserRegistrationRequest> getUserById(
//            @Parameter(description = "User id", required = true)
//            @PathVariable("id") String id){
//
//        UserRegistrationRequest userRegistrationRequest = userService.findUserById(id);
//        return new ResponseEntity<>(userRegistrationRequest, HttpStatus.OK);
//    }
//
//
//
//    // Private - ADMIN CAN CHECK USER STATUS BY ID AND UPDATE USER STATUS
//    @PutMapping("/status/{id}")
//    @Operation(summary = "Update User Status",
//            description= "Admin Update Status of an existing User by their ID.",
//            security = {@SecurityRequirement(name = "bearerAuth"), @SecurityRequirement(name = "oauth2")})
//
//    @PreAuthorize("hasRole('ADMIN')") // Only admins can change user status
//    public ResponseEntity<UserRegistrationRequest> updateUserStatusById(
//            @Parameter(description = "User id",required = true)
//            @PathVariable String id,
//
//            @Parameter(description = "New User Status", required = true)
//            @RequestParam UserStatus status) {
//        UserRegistrationRequest updatedUser = userService.updateUserStatus(id, status);
//        return ResponseEntity.ok(updatedUser);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDto> loginUser(
//            @RequestBody @Valid UserLoginRequest userLoginRequest) {
//        UserEntity user = userService.findByEmailAndPassword(userLoginRequest.getEmail(),
//                userLoginRequest.getPassword());
//
//        LoginResponseDto loginResponseDto = new LoginResponseDto(
//                user.getEmail(),
//                user.getPhoneNumber()
//        );
//
//        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
//
//
//
////        if (passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())){
////            return new ResponseEntity<>(user, HttpStatus.OK);
////        }else {
////            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
////        }
//
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logoutUser(){
//
//        return new ResponseEntity<>("User Logged Out Successfully", HttpStatus.OK);
//
//    }
}

