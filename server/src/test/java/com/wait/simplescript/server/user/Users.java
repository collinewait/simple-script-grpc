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
    public static final String ADMIN_ID = "someAdminId27658";
    public static final String ADMIN_EMAIL = "first@admin.com";
    public static final UserRole USER_ROLE = createUserRole();
    public static final Set<UserRole> USER_ROLES =
            Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(USER_ROLE)));
    public static final UserRole ADMIN_ROLE = createAdminRole();
    public static final Set<UserRole> ADMIN_ROLES =
            Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(ADMIN_ROLE)));

    private Users() {
        throw new AssertionError("com.wait.simplescript.server.user" +
                ".Users instances can not be created");
    }

    private static UserRole createUserRole() {
        UserRole role = new UserRole(ERole.USER);
        role.setId(ROLE_ID);
        return role;
    }

    private static UserRole createAdminRole() {
        UserRole role = new UserRole(ERole.ADMIN);
        role.setId("adminRoleId");
        return role;
    }

    public static User user() {
        User user = User.createUSer(FIRST_NAME, LAST_NAME, USER_EMAIL,
                PASSWORD, USER_ROLES);
        user.setId(USER_ID);
        return user;
    }

    public static User admin() {
        User user = User.createUSer(FIRST_NAME, LAST_NAME, ADMIN_EMAIL,
                PASSWORD, ADMIN_ROLES);
        user.setId(ADMIN_ID);
        return user;
    }
}
