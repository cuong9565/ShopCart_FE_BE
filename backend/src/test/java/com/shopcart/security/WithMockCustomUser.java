package com.shopcart.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "phuc@test.com";
    String userId() default "00000000-0000-0000-0000-000000000000"; // ID giả định
}