package com.wait.simplescript.server.infrastructure.security;

import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationUserDetails extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private static final String ROLE_PREFIX = "ROLE_";
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final Set<UserRole> roles;

    public ApplicationUserDetails(User user) {
        super(user.getEmail(), user.getPassword(),
                createAuthorities(user.getUserRoles()));
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.roles = user.getUserRoles();
    }

    private static Collection<SimpleGrantedAuthority> createAuthorities(Set<UserRole> roles) {
        return roles.stream().map(userRole -> new SimpleGrantedAuthority(ROLE_PREFIX + userRole.getName().name()))
                .collect(Collectors.toSet());
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }
}
