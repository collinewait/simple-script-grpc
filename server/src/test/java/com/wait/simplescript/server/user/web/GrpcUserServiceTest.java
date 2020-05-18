package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.*;
import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.infrastructure.security.WithMockAuthenticatedUser;
import com.wait.simplescript.server.script.Script;
import com.wait.simplescript.server.script.ScriptService;
import com.wait.simplescript.server.script.Scripts;
import com.wait.simplescript.server.user.*;
import com.wait.simplescript.server.user.role.UserRoleService;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(SpringProfiles.TEST)
public class GrpcUserServiceTest {
    @MockBean
    private UserService service;
    @MockBean
    private UserRoleService userRoleService;
    @MockBean
    private ScriptService scriptService;
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
            assertEquals(Users.USER_EMAIL,
                    results.get(0).getUsersList().get(0).getEmail());
        }

        @Test
        public void givenUserIsNotAdmin_thenAnExceptionShouldBeThrown() {
            UserListReq req = UserListReq.newBuilder()
                    .setAdminId(Users.ADMIN_ID)
                    .build();

            StreamRecorder<UserListRes> responseObserver = StreamRecorder
                    .create();
            Exception exception =
                    assertThrows(AuthenticationCredentialsNotFoundException.class,
                            () -> grpcUserService.getAllUsers(req,
                                    responseObserver));
            assertThat(exception.getMessage()).contains("An Authentication " +
                    "object was not found in the SecurityContext");
        }
    }

    @Nested
    class UpdateUser {
        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenValidDetails_thenResponseShouldContainUpdatedUser() throws Exception {
            String changedEmail = "wait@bob.com";
            User mockUser = User.createUSer("bob", "colline", "c@er.com",
                    "tiger", Users.USER_ROLES);
            mockUser.setId("bingo");
            User mockUpdatedUser = User.createUSer("mark", "col", changedEmail,
                    "paul", Users.USER_ROLES);
            mockUpdatedUser.setId("vol678");

            doReturn(Optional.of(mockUser))
                    .when(service).getUser(anyString());
            doReturn(Users.USER_ROLES)
                    .when(userRoleService).getUserRoles(anySet());
            doReturn(mockUpdatedUser)
                    .when(service).updateUser(any(User.class));

            UpdateUserReq req = UpdateUserReq.newBuilder()
                    .setUserId("userId")
                    .setUserUpdates(UserReq.newBuilder().setEmail(changedEmail).build())
                    .build();

            StreamRecorder<UserRes> responseObserver = StreamRecorder
                    .create();
            grpcUserService.updateUser(req, responseObserver);
            if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
                fail("The call did not terminate in time");
            }
            assertNull(responseObserver.getError());
            List<UserRes> results = responseObserver.getValues();
            assertEquals(1, results.size());
            assertThat(results).extracting(UserRes::getEmail).contains(changedEmail);
        }

        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenInvalid_thenUserNotFoundExceptionShouldBeThrown() {
            doThrow(new UserNotFoundException("fake"))
                    .when(service).getUser(anyString());

            UpdateUserReq req = UpdateUserReq.newBuilder()
                    .setUserId("userId")
                    .setUserUpdates(UserReq.newBuilder().setEmail("changed" +
                            "@Email.com").build())
                    .build();

            StreamRecorder<UserRes> responseObserver = StreamRecorder
                    .create();
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> grpcUserService.updateUser(req, responseObserver));
            assertThat(exception.getMessage()).contains("Could not find user " +
                    "with id fake");
        }
    }

    @Nested
    class GetUserWithScripts {
        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenValidUserId_thenUserScriptsShouldBeReturned() throws Exception {
            List<Script> scripts =
                    Arrays.asList(Scripts.MULTIPLE_OPERATIONS_SCRIPT,
                    Scripts.SINGLE_OPERATION_SCRIPT);

            doReturn(Optional.of(Users.user()))
                    .when(service).getUser(anyString());
            doReturn(scripts)
                    .when(scriptService).findByUser(anyString());

            SingleUserReq req = SingleUserReq.newBuilder()
                    .setUserId(Users.USER_ID)
                    .build();

            StreamRecorder<UserWithScripts> responseObserver = StreamRecorder
                    .create();
            grpcUserService.getUserWithScripts(req, responseObserver);
            if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
                fail("The call did not terminate in time");
            }
            assertNull(responseObserver.getError());
            List<UserWithScripts> results = responseObserver.getValues();
            assertEquals(1, results.size());
            assertEquals(2, results.get(0).getScriptsCount());
        }

        @Test
        @WithMockAuthenticatedUser(roles = {"admin"})
        public void givenInvalidUserId_thenUserNotFoundExceptionShouldBeThrown() {
            doThrow(new UserNotFoundException("fake"))
                    .when(service).getUser(anyString());

            SingleUserReq req = SingleUserReq.newBuilder()
                    .setUserId(Users.USER_ID)
                    .build();

            StreamRecorder<UserWithScripts> responseObserver = StreamRecorder
                    .create();
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> grpcUserService.getUserWithScripts(req, responseObserver));
            assertThat(exception.getMessage()).contains("Could not find user " +
                    "with id fake");
        }
    }
}
