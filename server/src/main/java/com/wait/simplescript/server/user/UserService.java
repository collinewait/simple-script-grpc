package com.wait.simplescript.server.user;

import java.util.Set;

public interface UserService {
    User createUser(String firstName, String lastName, String email, String password, Set<String> roles);

    boolean existsByEmail(String email);
}
