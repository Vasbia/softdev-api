package com.softdev.softdev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.service.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {
    

    @Autowired
    private UserService userService;
    // @GetMapping("/info")
    //  public String user(@AuthenticationPrincipal OAuth2User principal) {
    //        return "Hello, " + principal.getAttribute("name");
    //    }

    @GetMapping("/current_user_info")
    public UserDTO getCurrentUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        User user = userService.getCurrentUser(principal);
        return userService.toDto(user);
    }
    
    @GetMapping("/info/{email}")
    public UserDTO getUserCurrentUserInfo(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        return userService.toDto(user);
    }
    
}
