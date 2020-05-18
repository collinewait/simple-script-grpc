package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.UserListReq;
import com.wait.simplescript.lib.UserListRes;
import com.wait.simplescript.lib.UserReq;
import com.wait.simplescript.lib.UserRes;
import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.infrastructure.security.WithMockAuthenticatedUser;
import com.wait.simplescript.server.user.UserService;
import com.wait.simplescript.server.user.Users;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(SpringProfiles.TEST)
public class GrpcUserServiceTest {
    @MockBean
    private UserService service;
    @Autowired
    private GrpcUserService grpcUserService;

    @Nested
    class AddUser {
        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenValidUserDetails_thenResponseShouldContainUser() throws Exception {
            when(service.createUser(anyString(), anyString(), anyString(),
                    anyString(), anySet()))
                    .thenReturn(Users.user());
            when(service.existsByEmail(anyString())).thenReturn(false);

            UserReq req = UserReq.newBuilder()
                    .setFirstName(Users.FIRST_NAME)
                    .setLastName(Users.LAST_NAME)
                    .setEmail(Users.USER_EMAIL)
                    .setPassword(Users.PASSWORD)
                    .addRoles(Users.ROLE)
                    .build();

            StreamRecorder<UserRes> responseObserver = StreamRecorder
                    .create();
            grpcUserService.addUser(req, responseObserver);
            if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
                fail("The call did not terminate in time");
            }
            assertNull(responseObserver.getError());
            List<UserRes> results = responseObserver.getValues();
            assertEquals(1, results.size());
            assertThat(results).extracting(UserRes::getId).contains(Users.USER_ID);
        }
    }

    @Nested
    class GetAllUsers {
        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenUsersExist_thenResponseShouldContainUsers() throws Exception {
            doReturn(Collections.singletonList(Users.user()))
                    .when(service).findAllUsersExceptRequestingAdmin(anyString());

            UserListReq req = UserListReq.newBuilder()
                    .setAdminId(Users.ADMIN_ID)
                    .build();

            StreamRecorder<UserListRes> responseObserver = StreamRecorder
                    .create();
            grpcUserService.getAllUsers(req, responseObserver);
            if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
                fail("The call did not terminate in time");
            }
            assertNull(responseObserver.getError());
            List<UserListRes> results = responseObserver.getValues();
            assertEquals(1, results.size());
            assertEquals(1, results.get(0).getUsersList().size());
            assertEquals(Users.USER_EMAIL, results.get(0).getUsersList().get(0).getEmail());
        }

        @Test
        public void givenUserIsNotAdmin_thenAnExceptionShouldBeThrown() {
            UserListReq req = UserListReq.newBuilder()
                    .setAdminId(Users.ADMIN_ID)
                    .build();

            StreamRecorder<UserListRes> responseObserver = StreamRecorder
                    .create();
            Exception exception = assertThrows(AuthenticationCredentialsNotFoundException.class,
                    () -> grpcUserService.getAllUsers(req, responseObserver));
            assertThat(exception.getMessage()).contains("An Authentication object was not found in the SecurityContext");
        }
    }
}
