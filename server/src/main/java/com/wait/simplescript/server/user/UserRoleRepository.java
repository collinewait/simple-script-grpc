package com.wait.simplescript.server.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    Optional<UserRole> findByName(ERole name);
}
