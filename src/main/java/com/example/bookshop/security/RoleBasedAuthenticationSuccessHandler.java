package com.example.bookshop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Authentication success handler that redirects users based on their roles.
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/";

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/users";
                break;
            } else if (role.equals("ROLE_MODERATOR")) {
                redirectUrl = "/books";
                break;
            } else if (role.equals("ROLE_USER")) {
                redirectUrl = "/books/user/books";
                break;
            }
        }

        log.debug("Authenticated user '{}' with role redirected to {}", authentication.getName(), redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}

