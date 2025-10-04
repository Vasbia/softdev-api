package com.softdev.softdev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
// If you want to ignore CSRF only for some paths:
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Quick fix for 403 on POST for API backends:
            .csrf(csrf -> csrf.disable())
            // or, to ignore only some paths:
            // .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/public/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // keep these only if you actually use OAuth2 login/client
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }
}
