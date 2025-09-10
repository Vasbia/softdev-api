package com.softdev.softdev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.softdev.softdev.Security.OAuth2LoginSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2LoginSuccessHandler successHandler) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/user/current_user_info", "/api/user/secure/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/oauth2/authorization/google")
                .successHandler(successHandler)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(
                    new LoginUrlAuthenticationEntryPoint("/oauth2/authorization/google")
                )
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
            )
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
}
