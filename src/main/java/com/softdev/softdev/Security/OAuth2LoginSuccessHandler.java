package com.softdev.softdev.Security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.softdev.softdev.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final RequestCache requestCache;

    public OAuth2LoginSuccessHandler(UserService userService) {
        this.userService = userService;
        this.requestCache = new HttpSessionRequestCache();
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
            Authentication authentication) throws IOException, ServletException {
        
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        
        try {
            String email = oidcUser.getEmail();
            
        
            if (!userService.isUserExists(email)) {
                userService.CreateUser(oidcUser);
            }
            System.out.println("User :" + email);
            
            // Handle redirect
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                // Redirect to the original requested URL
                String targetUrl = savedRequest.getRedirectUrl();
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
            } else {
                // Redirect to default URL
                getRedirectStrategy().sendRedirect(request, response, getDefaultTargetUrl());
            }
            
        } catch (Exception e) {
            logger.error("Error during authentication success handling", e);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
