package com.softdev.softdev.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.softdev.softdev.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
            Authentication authentication) throws IOException, ServletException {
        
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        
        try {
            if (oidcUser == null) {
                throw new IllegalArgumentException("OIDC User is null");
            }

            String email = oidcUser.getAttribute("email");
            if(userService.isUserExists(email)){
                throw new IllegalArgumentException("User already exists with email: " + email);
            }


            System.out.println("Authenticated user email: " + email);

            userService.CreateUser(oidcUser);

        
        super.onAuthenticationSuccess(request, response, authentication);
    } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during authentication.");
        }
    }
}
