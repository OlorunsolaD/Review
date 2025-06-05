//package com.Sola.user_service.service.impl;
//
//import dto.com.sola.user_service.UserLoginRequest;
//import dto.com.sola.user_service.UserRegistrationRequest;
//import model.com.sola.user_service.UserEntity;
//import model.com.sola.user_service.UserStatus;
//import repository.com.sola.user_service.UserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//import static org.mockito.Mockito.*;
//
//
//class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//    private UserServiceImpl userService;
//    AutoCloseable autoCloseable;
//    UserEntity userEntity;
//    UserRegistrationRequest userRegistrationRequest;
//    UserLoginRequest userLoginRequest;
//    UserStatus userStatus;
//
//    @BeforeEach
//    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
//        userService = new UserServiceImpl(userRepository);
//        userRegistrationRequest = new UserRegistrationRequest();
//        userRegistrationRequest.setFullName("Discon Fillipe");
//        userRegistrationRequest.setEmail("DisconFillipe@gmail.com");
//        userRegistrationRequest.setPhoneNumber("90929289");
//        userRegistrationRequest.setPassword("D1234");
//
//        userEntity = UserEntity.builder()
//                .fullName(userRegistrationRequest.getFullName())
//                .email(userRegistrationRequest.getEmail())
//                .phoneNumber(userRegistrationRequest.getPhoneNumber())
//                .password(userRegistrationRequest.getPassword())
//                .build();
//    }
//
//
//    @AfterEach
//    void tearDown() throws Exception {
//        autoCloseable.close();
//    }
//
//    @Test
//    void createUserTest() {
//    when(userRepository.save(userEntity)).thenReturn(userEntity);
//
//        UserEntity createdUser = userService.createUser(userRegistrationRequest);
//
//        assertThat(createdUser).isNotNull();
//        assertThat(createdUser.getFullName()).isEqualTo("Discon Fillipe");
//        assertThat(createdUser.getEmail()).isEqualTo("DisconFillipe@gmail.com");
//        verify(userRepository, times(1)).save(any(UserEntity.class));
//
//
//        }
//
//
//    @Test
//    void updateUserTest() {
//        // Prepare initial user entity (existing user in the database)
//        String userId = "someUserId";
//        UserEntity existingUser = UserEntity.builder()
//                .id(userId)
//                .fullName("Old Full Name")
//                .email("OldEmail@gmail.com")
//                .phoneNumber("123456789")
//                .password("OldPassword")
//                .build();
//
//        // Mock the repository's findById method to return the existing user
//        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
//
//        // Create the updated user request
//        UserRegistrationRequest updatedUserRequest = new UserRegistrationRequest();
//        updatedUserRequest.setFullName("New Full Name");
//        updatedUserRequest.setEmail("NewEmail@gmail.com");
//        updatedUserRequest.setPhoneNumber("987654321");
//        updatedUserRequest.setPassword("NewPassword");
//
//        // Mock the save operation
//        when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);
//
//        // Call the updateUser method
//        UserEntity updatedUser = userService.updateUser(userId, updatedUserRequest);
//
//        // Assertions to verify the update
//        assertThat(updatedUser).isNotNull();
//        assertThat(updatedUser.getFullName()).isEqualTo("New Full Name");
//        assertThat(updatedUser.getEmail()).isEqualTo("NewEmail@gmail.com");
//
//        // Verify the save operation was called once with the updated entity
//        verify(userRepository, times(1)).save(any(UserEntity.class));
//    }
//
//
//    @Test
//    void getUserId() {
//    }
//
//    @Test
//    void findByEmailOrAddress() {
//    }
//
//    @Test
//    void updateUserStatus() {
//    }
//
//    @Test
//    void loginUserTest_Success() {
//
//        String email ="DisconFillipe@gmail.com";
//        String password = "D1234";
//        UserEntity user = new UserEntity();
//        user.setEmail(email);
//        user.setPassword(password);
//        when(userRepository.findByEmailAndPassword(email, password)).thenReturn(Optional.of(user));
//        UserEntity Outcomes = userService.LoginUser(email,password);
//
//        assertThat(Outcomes).isNotNull();
//        assertThat(Outcomes.getEmail()).isEqualTo(email);
//        assertThat(Outcomes.getPassword()).isEqualTo(password);
//        verify(userRepository, times(1)).findByEmailAndPassword(email, password);
//
//    }
//}