package com.wait.simplescript.server.infrastructure.security;

import com.wait.simplescript.server.user.UserRepository;
import com.wait.simplescript.server.user.Users;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationUserDetailsServiceTest {
    @Test
    public void testLoadingUserWithExistingUsername() {
        UserRepository userRepository = mock(UserRepository.class);
        ApplicationUserDetailsService service =
                new ApplicationUserDetailsService(userRepository);

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(Users.user()));

        UserDetails userDetails = service
                .loadUserByUsername(Users.USER_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(Users.USER_EMAIL);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
        assertThat(userDetails).isInstanceOfSatisfying(
                ApplicationUserDetails.class,
                applicationUserDetails -> assertThat(applicationUserDetails.getId())
                        .isEqualTo(Users.USER_ID));
    }

    @Test
    public void testLoadingUserWithNonExistingUsername() {
        UserRepository repository = mock(UserRepository.class);
        ApplicationUserDetailsService service =
                new ApplicationUserDetailsService(
                        repository);
        when(repository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(Users.USER_EMAIL));
        assertThat(exception.getMessage()).contains("User with email first@last.com could not be found");
    }
}
