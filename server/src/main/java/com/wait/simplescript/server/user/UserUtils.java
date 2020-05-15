package com.wait.simplescript.server.user;

public class UserUtils {
    public static final String EMAIL_ALREADY_EXISTS_ERROR_MSG = "Email " +
            "already exists";
    public static final String MISSING_FIELDS_MSG = "Some fields are missing," +
            " all fields are required";

    private UserUtils() {
        throw new AssertionError("com.wait.simplescript.server.user" +
                ".UserUtils instances can not be created");
    }
}
