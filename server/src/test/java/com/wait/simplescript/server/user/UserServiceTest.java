package com.wait.simplescript.server.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userRoleRepository = mock(UserRoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, userRoleRepository,
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
}
