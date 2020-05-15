package com.wait.simplescript.server.infrastructure.security;

import com.wait.simplescript.server.user.ERole;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRepository;
import com.wait.simplescript.server.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationUserDetailsServiceTest {
    final String EMAIL = "some@gmail.com";

    @Test
    public void testLoadingUserWithExistingUsername() {
        UserRepository userRepository = mock(UserRepository.class);
        ApplicationUserDetailsService service =
                new ApplicationUserDetailsService(userRepository);

        final String ID = "someUSerId";
        Set userRoles = new HashSet<>();
        userRoles.add(new UserRole(ERole.USER));
        User testUser = User.createUSer("first", "last",
                EMAIL, "mypass", userRoles);
        testUser.setId(ID);
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(testUser));

        UserDetails userDetails = service
                .loadUserByUsername(EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(EMAIL);
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
        assertThat(userDetails).isInstanceOfSatisfying(
                ApplicationUserDetails.class,
                applicationUserDetails -> assertThat(applicationUserDetails.getId())
                        .isEqualTo(ID));
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
                () -> service.loadUserByUsername(EMAIL));
        assertThat(exception.getMessage()).contains("User with email " +
                "some@gmail.com could not be found");
    }
}
