package com.wait.simplescript.server.user;

import java.util.Set;
import java.util.stream.Collectors;

public class UserUtils {
    public static final String EMAIL_ALREADY_EXISTS_ERROR_MSG = "Email " +
            "already exists";
    public static final String MISSING_FIELDS_MSG = "Some fields are missing," +
            " all fields are required";
    public static final String ADMIN_ROLE = "admin";
    public static final String USER_ROLE = "user";

    private UserUtils() {
        throw new AssertionError("com.wait.simplescript.server.user" +
                ".UserUtils instances can not be created");
    }

    public static Set<String> convertUserRolesSetToStringSet(Set<UserRole> rolesInUserRolesSet) {
        return rolesInUserRolesSet.stream().map(roleInSet -> {
            if (roleInSet.getName().equals(ERole.ADMIN)) {
                return ADMIN_ROLE;
            }
            return USER_ROLE;
        }).collect(Collectors.toSet());
    }
}
