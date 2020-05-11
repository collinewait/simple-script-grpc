package com.wait.simplescript.server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String firstName, String lastName, String email,
                           String password, Set<String> userRoles) {
        Set<UserRole> roles = new HashSet<>();
        userRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    UserRole adminRole = getUserRole(ERole.ADMIN);
                    roles.add(adminRole);
                    break;

                case "user":
                    UserRole userRole = getUserRole(ERole.USER);
                    roles.add(userRole);
                    break;
            }
        });
        User user = User.createUSer(firstName, lastName, email,
                passwordEncoder.encode(password),
                roles);
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserRole getUserRole(ERole roleName) {
        return userRoleRepository
                .findByName(roleName)
                .orElseThrow(
                        () -> new RuntimeException("Error" +
                                ": Role " +
                                "is not found."));
    }
}
