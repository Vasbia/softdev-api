package com.softdev.softdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.dto.auth.CreateUserDTO;
import com.softdev.softdev.dto.auth.LoginDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.service.AuthService;
import com.softdev.softdev.service.UserService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO) {

        String token = authService.login(loginDTO.getEmail(), loginDTO.getPassword());

        return token;
    }

    @PostMapping("/createUser")
    public UserDTO createUser(@Valid @ModelAttribute CreateUserDTO createUserDTO) {
       User user = authService.createUser(
            createUserDTO.getFname(),
            createUserDTO.getLname(),
            createUserDTO.getPassword(),
            createUserDTO.getEmail(),
            createUserDTO.getRole(),
            createUserDTO.getKey()

        );

        UserDTO userDTO = authService.toDTO(user);

        return userDTO;
    }
    

}
