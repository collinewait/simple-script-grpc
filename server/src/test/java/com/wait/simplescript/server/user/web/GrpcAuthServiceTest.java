package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.SignUpRequest;
import com.wait.simplescript.lib.SignUpResponse;
import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.user.UserService;
import com.wait.simplescript.server.user.UserUtils;
import com.wait.simplescript.server.user.Users;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
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

    @Test
    public void testSignUp() throws Exception {
        when(service.createUser(anyString(), anyString(), anyString(),
                anyString(), any(HashSet.class)))
                .thenReturn(Users.user());

        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(Users.FIRST_NAME)
                .setLastName(Users.LAST_NAME)
                .setEmail(Users.USER_EMAIL)
                .setPassword(Users.PASSWORD)
                .addRoles(Users.ROLE)
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
        assertThat(results).extracting(SignUpResponse::getId).contains(Users.USER_ID);
    }

    @Test
    public void testSignUpIfSomeFieldsAreEmpty() {
        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(Users.FIRST_NAME)
                .setLastName("")
                .setEmail(Users.USER_EMAIL)
                .setPassword(Users.PASSWORD)
                .addRoles(Users.ROLE)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        assertThat(exception.getMessage()).contains(UserUtils.MISSING_FIELDS_MSG);
    }

    @Test
    public void testSignUpIfSomeFieldsAreMissing() {
        SignUpRequest req = SignUpRequest.newBuilder()
                .setPassword(Users.PASSWORD)
                .addRoles(Users.ROLE)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        assertThat(exception.getMessage()).contains(UserUtils.MISSING_FIELDS_MSG);
    }

    @Test
    public void testSignUpIfEmailAlreadyExists() {
        when(service.existsByEmail(anyString()))
                .thenReturn(true);
        SignUpRequest req = SignUpRequest.newBuilder()
                .setFirstName(Users.FIRST_NAME)
                .setLastName(Users.LAST_NAME)
                .setEmail(Users.USER_EMAIL)
                .setPassword(Users.PASSWORD)
                .addRoles(Users.ROLE)
                .build();

        StreamRecorder<SignUpResponse> responseObserver = StreamRecorder
                .create();
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> grpcAuthService.signUp(req, responseObserver));
        assertThat(exception.getMessage()).contains(UserUtils.EMAIL_ALREADY_EXISTS_ERROR_MSG);
    }
}
