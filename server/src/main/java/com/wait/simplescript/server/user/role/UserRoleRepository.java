package com.wait.simplescript.server.user.role;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    Optional<UserRole> findByName(ERole name);
}
