package com.wait.simplescript.server.user;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Set<UserRole> getUserRoles(Set<String> roles) {
        return  roles.stream().map(role -> {
            if (role.equals("admin")) {
                return getUserRole(ERole.ADMIN);
            }
            return getUserRole(ERole.USER);
        }).collect(Collectors.toSet());
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
