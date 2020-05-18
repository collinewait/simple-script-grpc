package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.*;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.annotation.Secured;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wait.simplescript.server.user.UserUtils.convertUserRolesSetToStringSet;

@GrpcService
public class GrpcUserService extends UserServiceGrpc.UserServiceImplBase {
    private final UserService service;

    public GrpcUserService(UserService service) {
        this.service = service;
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
}
