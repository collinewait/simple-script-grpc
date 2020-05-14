package com.wait.simplescript.server.user;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    User createUser(String firstName, String lastName, String email, String password, Set<String> roles);
    Optional<User> getUser(String id);
    boolean existsByEmail(String email);
}
