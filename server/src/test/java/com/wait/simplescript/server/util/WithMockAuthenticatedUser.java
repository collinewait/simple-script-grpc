package com.wait.simplescript.server.util;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory =
        WithMockAuthenticatedUserSecurityContextFactory.class)
public @interface WithMockAuthenticatedUser {
    String firstName() default "waitire";

    String lastName() default "Colline";

    String email() default "col@wait.com";

    String[] roles() default {"user"};
}
