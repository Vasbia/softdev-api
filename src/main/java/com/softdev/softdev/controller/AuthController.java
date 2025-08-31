// package com.softdev.softdev.controller;

// import java.net.URI;

// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.softdev.softdev.service.AuthService;

// @RestController
// public class AuthController {

//     private final AuthService authService;

//     public AuthController(AuthService authService) {
//         this.authService = authService;
//     }

//     @GetMapping("/auth/login")
//     public ResponseEntity<Void> login() {
//         HttpHeaders h = new HttpHeaders();
//         h.setLocation(URI.create("/oauth2/authorization/google"));

//         //if create not exist user'
//         if(!authService.isUserExists()){
//             authService.createUser();
//         }
        
//         return new ResponseEntity<>(h, HttpStatus.FOUND); // 302
//     }
// }
