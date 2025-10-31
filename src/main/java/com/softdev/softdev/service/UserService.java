package com.softdev.softdev.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.repository.UserRepository;
import com.softdev.softdev.security.jwtUtil;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    public boolean isUserExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getCurrentUser(String token) {

        if (jwtUtil.verifyJwt(token)){

            Map<String, Object> data = jwtUtil.extractToken(token);
            String gmail = (String) data.get("gmail");
            User user = userRepository.findByEmail(gmail);
    
             return user;
        }

        throw new ResourceNotFoundException("Invalid Token!!");
        
    }


    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
    
        UserDTO userDTO = new UserDTO();
        userDTO.setFname(user.getFname());
        userDTO.setLname(user.getLname());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

}
