
import com.Sola.user_service.dto.UserRegistrationRequest;
import com.Sola.user_service.exception.UserNotFoundException;
import com.Sola.user_service.model.UserEntity;
import com.Sola.user_service.model.UserStatus;
import com.Sola.user_service.service.UserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserService userService() {
        return new UserService() {
            @Override
            public UserEntity createUser(UserRegistrationRequest userRegistrationRequest) {
                return null;
            }

            @Override
            public UserEntity findByEmailAndPassword(String email, String rawPassword) throws UserNotFoundException {
                return null;
            }

            @Override
            public UserEntity updateUser(String id, UserRegistrationRequest userRegistrationRequest) {
                return null;
            }

            @Override
            public UserRegistrationRequest updateUserStatus(String id, UserStatus status) throws UserNotFoundException {
                return null;
            }

            @Override
            public UserRegistrationRequest findUserById(String userId) throws UserNotFoundException {
                return null;
            }

            @Override
            public UserEntity findUserStatusById(String id) {
                return null;
            }
        };
    }
}