package com.wait.simplescript.server.user.web;

import com.wait.simplescript.lib.UserReq;
import com.wait.simplescript.lib.UserRes;
import com.wait.simplescript.lib.UserServiceGrpc;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.access.annotation.Secured;

import java.util.HashSet;
import java.util.Set;

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
}
