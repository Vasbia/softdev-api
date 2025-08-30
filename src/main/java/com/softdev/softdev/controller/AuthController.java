package com.softdev.softdev.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    @GetMapping("/auth/login")
    public ResponseEntity<Void> login() {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create("/oauth2/authorization/google"));
        return new ResponseEntity<>(h, HttpStatus.FOUND); // 302
    }
}
