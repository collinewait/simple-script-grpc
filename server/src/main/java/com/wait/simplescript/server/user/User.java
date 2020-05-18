package com.wait.simplescript.server.user;

import com.wait.simplescript.server.user.role.UserRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @DBRef
    private Set<UserRole> userRoles;

    protected User() {
    }

    public User(String firstName, String lastName, String email,
                String password, Set<UserRole> userRoles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userRoles = userRoles;
    }

    public static User createUSer(String firstName, String lastName,
                                  String email, String password,
                                  Set<UserRole> userRoles) {
        return new User(firstName, lastName, email, password, userRoles);
    }
}
