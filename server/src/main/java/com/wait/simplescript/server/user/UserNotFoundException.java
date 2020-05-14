package com.wait.simplescript.server.user;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String id) {
        super(String.format("Could not find user with id %s", id));
    }
}
