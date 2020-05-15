package com.wait.simplescript.server.script.web;

import com.wait.simplescript.lib.ScriptOperationsReq;
import com.wait.simplescript.lib.ScriptRes;
import com.wait.simplescript.server.infrastructure.SpringProfiles;
import com.wait.simplescript.server.infrastructure.security.WithMockAuthenticatedUser;
import com.wait.simplescript.server.script.InvalidOperationException;
import com.wait.simplescript.server.script.Script;
import com.wait.simplescript.server.script.ScriptService;
import com.wait.simplescript.server.script.ScriptUtils;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserService;
import com.wait.simplescript.server.user.Users;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles(SpringProfiles.TEST)
public class GrpcScriptServiceTest {
    private final String scriptId = "23456scriptId356";
    @MockBean
    private ScriptService scriptService;
    @MockBean
    private UserService userService;
    @Autowired
    private GrpcScriptService grpcScriptService;

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithSingleOperation() throws Exception {
        Script testScript = Script.createScript(Users.user(),
                ScriptUtils.DO_THIS,
                new ArrayList<>());
        testScript.setId(scriptId);

        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));
        when(scriptService.createScript(any(User.class), anyString()))
                .thenReturn(testScript);

        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(Collections.singletonList(ScriptUtils.DO_THIS))
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();
        grpcScriptService.createScript(req, responseObserver);
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());
        List<ScriptRes> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertThat(results).extracting(ScriptRes::getId).contains
                (scriptId);
        assertThat(results).extracting(ScriptRes::getScriptValue).contains
                (ScriptUtils.DO_THIS);
    }

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithMultipleOperations() throws Exception {
        String expectedStringValue = String.format("%s\n%s",
                ScriptUtils.DO_THIS,
                ScriptUtils.DO_THIS);
        Script testScript = Script.createScript(Users.user(),
                expectedStringValue,
                new ArrayList<>());
        testScript.setId(scriptId);

        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));
        when(scriptService.createScript(any(User.class), anyString()))
                .thenReturn(testScript);

        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(Arrays.asList(ScriptUtils.DO_THIS,
                        ScriptUtils.DO_THIS))
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();
        grpcScriptService.createScript(req, responseObserver);
        if (!responseObserver.awaitCompletion(5, TimeUnit.SECONDS)) {
            fail("The call did not terminate in time");
        }
        assertNull(responseObserver.getError());
        List<ScriptRes> results = responseObserver.getValues();
        assertEquals(1, results.size());
        assertThat(results).extracting(ScriptRes::getId).contains
                (scriptId);
        assertThat(results).extracting(ScriptRes::getScriptValue).contains
                (expectedStringValue);
    }

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithEmptyOperations() {
        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));

        List<String> operations = new ArrayList<>();
        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(operations)
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();

        Exception exception = assertThrows(InvalidOperationException.class,
                () -> grpcScriptService.createScript(req, responseObserver));
        assertThat(exception.getMessage()).contains(ScriptUtils.MISSING_OPERATIONS_MSG);
    }

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithSingleInvalidOperation() {
        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));

        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(Collections.singletonList(
                        "SomeInvalidOperationHere"))
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();

        Exception exception = assertThrows(InvalidOperationException.class,
                () -> grpcScriptService.createScript(req, responseObserver));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithMultipleInvalidOperations() {
        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));

        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(Arrays.asList("SomeInvalidOperationHere", ""))
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();

        Exception exception = assertThrows(InvalidOperationException.class,
                () -> grpcScriptService.createScript(req, responseObserver));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }

    @Test
    @WithMockAuthenticatedUser
    public void testCreateScriptWithLatInvalidOperation() {
        when(userService.getUser(anyString()))
                .thenReturn(Optional.of(Users.user()));

        ScriptOperationsReq req = ScriptOperationsReq.newBuilder()
                .addAllOperations(Arrays.asList(ScriptUtils.DO_THIS, ""))
                .build();

        StreamRecorder<ScriptRes> responseObserver = StreamRecorder
                .create();

        Exception exception = assertThrows(InvalidOperationException.class,
                () -> grpcScriptService.createScript(req, responseObserver));
        assertThat(exception.getMessage()).contains(ScriptUtils.INVALID_OPERATIONS_MSG);
    }
}
