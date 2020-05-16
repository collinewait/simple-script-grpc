package com.wait.simplescript.server.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Users {
    public static final String USER_ID = "321someuserId123";
    public static final String ROLE_ID = "898somerole30988gh";
    public static final String FIRST_NAME = "first";
    public static final String LAST_NAME = "last";
    public static final String USER_EMAIL = "first@last.com";
    public static final String PASSWORD = "mypass";
    public static final String ROLE = "user";
    public static final UserRole USER_ROLE = createUserRole();
    public static final Set<UserRole> USER_ROLES =
            Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(USER_ROLE)));

    private Users() {
        throw new AssertionError("com.wait.simplescript.server.user" +
                ".Users instances can not be created");
    }

    public static User user() {
        User user = User.createUSer(FIRST_NAME, LAST_NAME, USER_EMAIL,
                PASSWORD, USER_ROLES);
        user.setId(USER_ID);
        return user;
    }

    private static UserRole createUserRole() {
        UserRole role = new UserRole(ERole.USER);
        role.setId(ROLE_ID);
        return role;
    }
}
