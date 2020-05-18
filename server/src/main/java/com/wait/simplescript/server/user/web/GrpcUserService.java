package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.*;
import com.wait.simplescript.server.script.Script;
import com.wait.simplescript.server.script.ScriptService;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserNotFoundException;
import com.wait.simplescript.server.user.role.UserRoleService;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wait.simplescript.server.user.UserUtils.convertUserRolesSetToStringSet;

@GrpcService
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final ScriptService scriptService;

    public GrpcUserService(UserService service,
                           PasswordEncoder passwordEncoder,
                           UserRoleService userRoleService,
                           ScriptService scriptService) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.userRoleService = userRoleService;
        this.scriptService = scriptService;
    }

    @Override
    @Secured({"ROLE_ADMIN"})
    public void addUser(UserReq req,
                        StreamObserver<UserRes> responseObserver) {
        String firstName = req.getFirstName();
        String lastName = req.getLastName();
        String email = req.getEmail();
        String password = req.getPassword();
        Set<String> roles = new HashSet<>(req.getRolesList());

        User user = service.createUser(firstName, lastName,
                email, password, roles);
        Set<String> userRoles =
                convertUserRolesSetToStringSet(user.getUserRoles());

        UserRes res = UserRes.newBuilder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .addAllRoles(userRoles).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    @Secured({"ROLE_ADMIN"})
    public void getAllUsers(UserListReq req,
                            StreamObserver<UserListRes> responseObserver) {

        List<User> users =
                service.findAllUsersExceptRequestingAdmin(req.getAdminId());

        List<UserRes> userList = users.stream().map(user -> UserRes.newBuilder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .addAllRoles(convertUserRolesSetToStringSet(user.getUserRoles()))
                .build()).collect(Collectors.toList());

        UserListRes res = UserListRes.newBuilder()
                .addAllUsers(userList)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public void updateUser(UpdateUserReq req,
                           StreamObserver<UserRes> responseObserver) {
        String userId = req.getUserId();
        UserReq userUpdates = req.getUserUpdates();

        User user = service.getUser(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        if (!(userUpdates.getFirstName().isEmpty())) {
            user.setFirstName(userUpdates.getFirstName());
        }
        if (!(userUpdates.getLastName().isEmpty())) {
            user.setLastName(userUpdates.getLastName());
        }
        if (!(userUpdates.getEmail().isEmpty())) {
            user.setEmail(userUpdates.getEmail());
        }
        if (!(userUpdates.getPassword().isEmpty())) {
            user.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
        }
        if (!(userUpdates.getRolesList().isEmpty())) {
            user.setUserRoles(userRoleService
                    .getUserRoles(new HashSet<>(userUpdates.getRolesList())));
        }

        User updatedUser = service.updateUser(user);
        Set<String> userRoles =
                convertUserRolesSetToStringSet(updatedUser.getUserRoles());

        UserRes res = UserRes.newBuilder()
                .setId(updatedUser.getId())
                .setFirstName(updatedUser.getFirstName())
                .setLastName(updatedUser.getLastName())
                .setEmail(updatedUser.getEmail())
                .addAllRoles(userRoles).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    @Secured({"ROLE_ADMIN"})
    public void getUserWithScripts(SingleUserReq req,
                                   StreamObserver<UserWithScripts> responseObserver) {
        String userId = req.getUserId();

        User user = service.getUser(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        List<Script> scripts = scriptService.findByUser(userId);
        List<ScriptRes> scriptList = scripts.stream().map(script -> ScriptRes.newBuilder()
                .setId(script.getId())
                .setScriptValue(script.getScriptValue())
                .addAllExecutedOutput(script.getExecutedOutput())
                .build()).collect(Collectors.toList());

                Set <String > userRoles =
                        convertUserRolesSetToStringSet(user.getUserRoles());

        UserWithScripts res = UserWithScripts.newBuilder()
                .setUser(UserRes.newBuilder()
                        .setId(user.getId())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setEmail(user.getEmail())
                        .addAllRoles(userRoles).build())
                .addAllScripts(scriptList)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
