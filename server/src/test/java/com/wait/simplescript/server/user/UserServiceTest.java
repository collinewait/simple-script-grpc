package com.wait.simplescript.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserRoleService userRoleService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userRoleService = mock(UserRoleService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, userRoleService,
                passwordEncoder);
    }

    @Nested
    class GetAllUsers {
        @Test
        public void givenUsersExist_thenUsersShouldBeReturned() {
            doReturn(Collections.singletonList(Users.user()))
                    .when(userRepository).findByIdNot(anyString());

            List<User> users =
                    userService.findAllUsersExceptRequestingAdmin(Users.ADMIN_ID);
            assertEquals(1, users.size());
            assertThat(users).extracting(User::getEmail).contains(Users.USER_EMAIL);
            assertThat(users).extracting(User::getEmail).doesNotContain(Users.ADMIN_EMAIL);
        }
    }

    @Nested
    class UpdateUser {
        @Test
        public void givenValidDetails_thenUpdatedUserShouldBeReturned() {
            doReturn(Users.user())
                    .when(userRepository).save(any(User.class));

            User user = userService.updateUser(Users.user());
            assertEquals(Users.USER_EMAIL, user.getEmail());
        }
    }
}
