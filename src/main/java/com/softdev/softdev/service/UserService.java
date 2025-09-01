package com.softdev.softdev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if(principal == null) {
            return null;
        }
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email);
    }

    public void CreateUser(@AuthenticationPrincipal OAuth2User principal){
        if(principal == null) {
            return;
        }
        String email = principal.getAttribute("email");
        if(isUserExists(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setFname(principal.getAttribute("given_name"));
        System.out.println("Family Name: " + principal.getAttribute("family_name"));
        user.setLname(principal.getAttribute("family_name"));
        user.setProvider("Google");
        user.setProviderId(principal.getAttribute("sub"));

        userRepository.save(user);
    }

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
    
        UserDTO userDTO = new UserDTO();
        userDTO.setFname(user.getFname());
        userDTO.setLname(user.getLname());
        userDTO.setEmail(user.getEmail());
        
        return userDTO;
    }
}
