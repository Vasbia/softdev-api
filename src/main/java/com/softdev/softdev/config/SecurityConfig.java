package com.softdev.softdev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",                 // หน้าเปิดเล่น
                    "/auth/login",       // จุดเริ่ม login แบบ custom
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            // ใช้ OAuth2 Login และกำหนด custom login page = /auth/login
            .oauth2Login(oauth -> oauth
                .loginPage("/auth/login")
                .defaultSuccessUrl("/", true) // หลัง login สำเร็จ ให้กลับหน้า /
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            // ปรับตาม use case: ถ้าเป็น pure REST อาจต้อง disable
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
