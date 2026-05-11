package com.shopcart.security;

import com.shopcart.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.UUID;

public class MockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Tạo đối tượng User thật của bạn
        User user = new User();
        user.setId(UUID.fromString(annotation.userId()));
        user.setEmail(annotation.username());
        user.setHashPassword("password");

        // Bọc vào CustomUserDetails
        CustomUserDetails principal = new CustomUserDetails(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}