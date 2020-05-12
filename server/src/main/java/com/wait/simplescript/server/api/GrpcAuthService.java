package com.wait.simplescript.server.api;

import com.wait.simplescript.lib.AuthServiceGrpc;
import com.wait.simplescript.lib.SignInRequest;
import com.wait.simplescript.lib.SignUpRequest;
import com.wait.simplescript.lib.SignUpResponse;
import com.wait.simplescript.server.security.ApplicationUserDetails;
import com.wait.simplescript.server.user.ERole;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRole;
import com.wait.simplescript.server.user.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@GrpcService
public class GrpcAuthService extends AuthServiceGrpc.AuthServiceImplBase {
    private final UserService service;
    private final AuthenticationManager authenticationManager;

    public GrpcAuthService(UserService service,
                           AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
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

    @Override
    public void signIn(SignInRequest req,
                       StreamObserver<SignUpResponse> responseObserver) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(),
                        req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ApplicationUserDetails user =
                (ApplicationUserDetails) authentication.getPrincipal();
        Set<String> userRoles =
                convertUserRolesSetToStringSet(user.getRoles());

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
