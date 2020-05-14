package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.SignUpRequest;
import com.wait.simplescript.lib.SignUpResponse;
import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.user.ERole;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRole;
import com.wait.simplescript.server.user.UserService;
import com.wait.simplescript.server.user.web.GrpcAuthService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(SpringProfiles.TEST)
public class GrpcAuthServiceTest {
    @MockBean
    private UserService service;
    @Autowired
    private GrpcAuthService grpcAuthService;

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<UserRole> userRoles;
    private String userRole;

    @BeforeEach
    public void setup() {
        userId = "321someuserId123";
        firstName = "first";
        lastName = "last";
        email = "first@last.com";
        password = "mypass";
        userRole = "user";
        userRoles = new HashSet<>();
        userRoles.add(new UserRole(ERole.USER));
    }

    @Test
    public void testSignUp() throws Exception {
        User testUser = new User(firstName, lastName, email, password,
                userRoles);
        testUser.setId(userId);
        when(service.createUser(anyString(), anyString(), anyString(),
                anyString(), any(HashSet.class)))
                .thenReturn(testUser);

        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(password)
                .addRoles(userRole)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        grpcAuthService.signUp(req, responseObserver);
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());
        List<SignUpResponse> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertThat(results).extracting(SignUpResponse::getId).contains(userId);
    }

    @Test
    public void testSignUpIfSomeFieldsAreEmpty() {
        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName("")
                .setEmail(email)
                .setPassword(password)
                .addRoles(userRole)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        String expectedMessage = "Some fields are missing, all fields are " +
                "required";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    public void testSignUpIfSomeFieldsAreMissing() {
        SignUpRequest req = SignUpRequest.newBuilder()
                .setPassword(password)
                .addRoles(userRole)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        String expectedMessage = "Some fields are missing, all fields are " +
                "required";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    public void testSignUpIfEmailAlreadyExists() {
        when(service.existsByEmail(anyString()))
                .thenReturn(true);
        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPassword(password)
                .addRoles(userRole)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        String expectedMessage = "Email already exists";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }
}
