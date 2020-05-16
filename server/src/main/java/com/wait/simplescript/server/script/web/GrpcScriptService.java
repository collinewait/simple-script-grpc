package com.wait.simplescript.server.script.web;

import com.wait.simplescript.lib.*;
import com.wait.simplescript.server.infrastructure.security.ApplicationUserDetails;
import com.wait.simplescript.server.script.Script;
import com.wait.simplescript.server.script.ScriptNotFoundException;
import com.wait.simplescript.server.script.ScriptService;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserNotFoundException;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

import static com.wait.simplescript.server.script.ScriptUtils.generateScript;

@GrpcService
public class GrpcScriptService extends ScriptServiceGrpc.ScriptServiceImplBase {
    private final ScriptService scriptService;
    private final UserService userService;

    public GrpcScriptService(ScriptService scriptService,
                             UserService userService) {
        this.scriptService = scriptService;
        this.userService = userService;
    }

    @Override
    @Secured("ROLE_USER")
    public void createScript(ScriptOperationsReq req,
                             StreamObserver<ScriptRes> responseObserver) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        ApplicationUserDetails userDetails =
                (ApplicationUserDetails) authentication.getPrincipal();
        User user = userService.getUser(userDetails.getId()).orElseThrow(
                () -> new UserNotFoundException(userDetails.getId()));
        List<String> operations = req.getOperationsList();

        String scriptValue = generateScript(operations);
        Script script = scriptService.createScript(user, scriptValue);
        ScriptRes res = ScriptRes.newBuilder()
                .setId(script.getId())
                .setScriptValue(script.getScriptValue())
                .addAllExecutedOutput(script.getExecutedOutput())
                .setUserId(script.getUser().getId())
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    @Secured("ROLE_USER")
    public void getScript(SingleScriptReq req,
                          StreamObserver<ScriptRes> responseObserver) {
        Script script =
                scriptService.findById(req.getId()).orElseThrow(
                        () -> new ScriptNotFoundException(req.getId()));
        ScriptRes res = ScriptRes.newBuilder()
                .setId(script.getId())
                .setScriptValue(script.getScriptValue())
                .addAllExecutedOutput(script.getExecutedOutput())
                .setUserId(script.getUser().getId())
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    @Secured("ROLE_USER")
    public void getAllScripts(EmptyReq req,
                              StreamObserver<ScriptListRes> responseObserver) {
        List<Script> scripts = scriptService.findAll();

        List<ScriptRes> scriptsResList = scripts.stream().map(script -> ScriptRes.newBuilder()
                .setId(script.getId())
                .setScriptValue(script.getScriptValue())
                .addAllExecutedOutput(script.getExecutedOutput())
                .setUserId(script.getUser().getId())
                .build()).collect(Collectors.toList());

        ScriptListRes res =
                ScriptListRes.newBuilder().addAllScripts(scriptsResList).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
