package com.wait.simplescript.server.api;

import com.wait.simplescript.lib.AuthServiceGrpc;
import com.wait.simplescript.lib.SignUpRequest;
import com.wait.simplescript.lib.SignUpResponse;
import com.wait.simplescript.server.user.ERole;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRole;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
public class GrpcAuthService extends AuthServiceGrpc.AuthServiceImplBase {
    private final UserService service;

    public GrpcAuthService(UserService service) {
        this.service = service;
    }

    @Override
    public void signUp(SignUpRequest req,
                       StreamObserver<SignUpResponse> responseObserver) {
        String firstName = req.getFirstName();
        String lastName = req.getLastName();
        String email = req.getEmail();
        String password = req.getPassword();
        Set<String> roles = new HashSet<>(req.getRolesList());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || roles.isEmpty()) {
            throw new IllegalArgumentException("Some fields are missing, all " +
                    "fields are required");
        }

        if (service.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = service.createUser(firstName, lastName,
                email, password, roles);
        Set<String> userRoles =
                convertUserRolesSetToStringSet(user.getUserRoles());
        SignUpResponse res = SignUpResponse.newBuilder()
                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .addAllRoles(userRoles).build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    private Set<String> convertUserRolesSetToStringSet(Set<UserRole> rolesInUserRolesSet) {
        return rolesInUserRolesSet.stream().map(roleInSet -> {
            if (roleInSet.getName().equals(ERole.ADMIN)) {
                return "admin";
            }
            return "user";
        }).collect(Collectors.toSet());
    }
}
