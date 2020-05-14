package com.wait.simplescript.server.util;

import com.wait.simplescript.server.security.ApplicationUserDetails;
import com.wait.simplescript.server.user.ERole;
import com.wait.simplescript.server.user.User;
import com.wait.simplescript.server.user.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class WithMockAuthenticatedUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockAuthenticatedUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAuthenticatedUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, ERole> roleMap = new HashMap<>();
        roleMap.put("admin", ERole.ADMIN);
        roleMap.put("user", ERole.USER);
        Set<UserRole> userRoles = new HashSet<>();
        for (String role : mockUser.roles()) {
            if (!(roleMap.containsKey(role.toLowerCase()))) {
                throw new IllegalArgumentException("You provided an invalid " +
                        "Role. admin and user are the valid roles");
            }
            userRoles.add(new UserRole(roleMap.get(role.toLowerCase())));
        }
        User user = User.createUSer(mockUser.firstName(), mockUser.lastName()
                , mockUser.email(), "password",
                userRoles);
        user.setId("userId");
        ApplicationUserDetails principal =
                new ApplicationUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password"
                        , principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
