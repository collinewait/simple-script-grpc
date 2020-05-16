package com.wait.simplescript.server.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Getter @EqualsAndHashCode @ToString
public class UserRole {
    @Id
    private String id;
    private ERole name;
    protected UserRole() {}
    public UserRole(ERole name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
