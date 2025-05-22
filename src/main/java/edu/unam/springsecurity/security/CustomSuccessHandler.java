package edu.unam.springsecurity.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin";
                break;
            } else if (auth.getAuthority().equals("ROLE_TECNICO")) {
                redirectUrl = "/tecnico";
                break;
            } else if (auth.getAuthority().equals("ROLE_USER")) {
                redirectUrl = "/user";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
