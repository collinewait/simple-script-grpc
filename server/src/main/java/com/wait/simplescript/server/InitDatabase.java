package com.wait.simplescript.server;

import com.wait.simplescript.server.user.role.ERole;
import com.wait.simplescript.server.user.role.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase {
    @Bean
    CommandLineRunner init(MongoOperations operations) {
        return args -> {
            operations.dropCollection(UserRole.class);

            operations.insert(new UserRole(ERole.ADMIN));
            operations.insert(new UserRole(ERole.USER));

            operations.findAll(UserRole.class).forEach(role -> System.out.println(role.toString()));
        };
    }
}
