package com.wait.simplescript.server.user;

import java.util.Set;

public interface UserRoleService {
    Set<UserRole> getUserRoles(Set<String> roles);
}
