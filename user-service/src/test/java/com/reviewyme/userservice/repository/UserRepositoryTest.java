//package com.Sola.user_service.repository;
//
//import model.com.sola.user_service.UserEntity;
//import model.com.sola.user_service.UserStatus;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//
//@DataMongoTest
//public class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//    private UserEntity user1;
//    private UserEntity user2;
//
//
//    @BeforeEach
//    void setUp() {
//        user1 = new UserEntity("12349", "Corn", "r1234",
//                "Maze Down", "Cornith@gmail.com", "Building Street 1256",
//                "08901727", "client", "resume123", UserStatus.ACTIVE);
//
//        user2 = new UserEntity("3498", "Gold", "g1234",
//                "Gaze Pown", "Goldith@gmail.com", "Building Street 34562",
//                "99801727", "client", "resume550", UserStatus.INACTIVE);
//
//        userRepository.save(user1);
//        userRepository.save(user2);
//
//
//    }
//
//    @AfterEach
//    void tearDown() {
//        userRepository.deleteAll();
//
//    }
//
//    // Test Case to Pass
//    @Test
//    void findByEmailOrAddress_Found() {
//
//        List<UserEntity> foundUsers = userRepository.findByEmailOrAddress(null,
//                "Building Street 1256");
//        assertThat(foundUsers.get(0).getEmail()).isEqualTo(user1.getEmail());
//        assertThat(foundUsers.get(0).getAddress()).isEqualTo(user1.getAddress());
//
//    }
//
//    // Test Case to Pass if EmailOrAddress Not Found
//    @Test
//    void findByEmailOrAddress_NotFound() {
//        List<UserEntity> foundUsers = userRepository.findByEmailOrAddress("doesnotexist@gmail.com",
//                "Nonexistent Address");
//        assertThat(foundUsers.isEmpty()).isTrue();
//
//    }
//
//    // Test Case to Success
//    @Test
//    void findUsernameOrPassword_Found(){
//        Optional<UserEntity> FoundUserDetails= userRepository.findByEmailAndPassword("Goldith@gmail.com",
//                "g1234");
//        assertThat(FoundUserDetails.isPresent());
//        assertThat(FoundUserDetails.get().getUsername()).isEqualTo("Gold");
//
//    }
//
//    // Test Case to Pass if no Username or Password Found
//    @Test
//    void findUsernameOrPassword_NotFound(){
//        // Test when email and password don't match
//        Optional<UserEntity> FoundUserDetails= userRepository.findByEmailAndPassword("notexist@gmail.com",
//                "blank");
//        // Validate that no user is found
//        assertThat(FoundUserDetails).isNotPresent();
//    }
//
//    @Test
//    void testFindById() {
//        // Attempt to retrieve the user by ID
//        Optional<UserEntity> foundUser = userRepository.findById("12349");
//
//        // Validate that the user was found and matches the expected values
//        assertThat(foundUser).isPresent();
//        assertThat(foundUser.get().getUsername()).isEqualTo("Corn");
//        assertThat(foundUser.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
//    }
//
//
//
//
//}
//
//
