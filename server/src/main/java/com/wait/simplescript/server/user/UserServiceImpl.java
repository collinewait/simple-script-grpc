package com.wait.simplescript.server.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserRoleService userRoleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(String firstName, String lastName, String email,
                           String password, Set<String> userRoles) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || userRoles.isEmpty()) {
            throw new IllegalArgumentException(UserUtils.MISSING_FIELDS_MSG);
        }

        if (existsByEmail(email)) {
            throw new IllegalArgumentException(UserUtils.EMAIL_ALREADY_EXISTS_ERROR_MSG);
        }

        Set<UserRole> roles = userRoleService.getUserRoles(userRoles);

        User user = User.createUSer(firstName, lastName, email,
                passwordEncoder.encode(password),
                roles);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(String id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAllUsersExceptRequestingAdmin(String id) {
        return userRepository.findByIdNot(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

}
