package com.softdev.softdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.dto.auth.CreateUserDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/current_user_info")
    public UserDTO getCurrentUserInfo(String token) {
        User user = userService.getCurrentUser(token);
        return userService.toDto(user);
    }
    
    @GetMapping("/info/{email}")
    public UserDTO getUserCurrentUserInfo(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        return userService.toDto(user);
    }

    @GetMapping("/secure/test")
    public String securedEndpoint() {
        return "This is a secured endpoint!";   
    }

    @GetMapping("/genToken")
    public String getMethodName(@RequestParam String email) {
        String token = userService.GenToken(email);

        return token;
    }
    
}
