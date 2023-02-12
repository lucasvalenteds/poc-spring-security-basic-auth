package com.example.testing;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithSecurityUserFactory.class)
public @interface WithSecurityUser {

    long id() default 1L;

    String username();

    String password() default "123456";

    String[] authorities() default {};
}
