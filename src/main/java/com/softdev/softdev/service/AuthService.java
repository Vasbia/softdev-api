package com.softdev.softdev.service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.softdev.softdev.dto.User.UserDTO;
import com.softdev.softdev.entity.User;
import com.softdev.softdev.exception.ResourceNotFoundException;
import com.softdev.softdev.exception.user.UserForBiddenException;
import com.softdev.softdev.exception.user.UserNotAuthenticatedException;
import com.softdev.softdev.repository.UserRepository;
import com.softdev.softdev.security.jwtUtil;
@Service
public class AuthService {
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Value("${jwt.key_admin}")
    private String keyAdmin;

    @Value("${jwt.secret_key}")
    private String secretKey;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(input.getBytes());
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new UserNotAuthenticatedException("Error creating hash");
        }
    }

    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
            hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    private String generateSalt(){
        StringBuilder salt = new StringBuilder(5);
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(CHARACTERS.length());
            salt.append(CHARACTERS.charAt(index));
        }
        return salt.toString();
    }

    public String login(String email, String password){
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new ResourceNotFoundException("User not found by this email");
        }

        String salt = user.getSalt();
        String password_salt = password + salt;

        if (hash(password_salt).equals(user.getPassword())){

            Map<String, Object> payload = Map.of(
                "sub", user.getFname() + " " + user.getLname() ,
                "user_id", user.getUserId(),
                "gmail" , user.getEmail(),
                "iat", System.currentTimeMillis() / 1000 + (3600*3) // 3 hour expiration
            );

            String secret = secretKey;
            String token = jwtUtil.generateToken(payload, secret);

            return token;
        }


        throw new UserNotAuthenticatedException("Email or Password Invalid!");
    
    }

    public User createUser(String fname , String lname, String password ,String email, String role , String key ){

        if (key.equals(keyAdmin)){

            if (role.equals("USER") || role.equals("BUS_DRIVER")){

                if (userRepository.existsByEmail(email)){
                    throw new UserForBiddenException("Email already exists");
                }

                String salt = generateSalt();
                String password_hash_salting = hash(password + salt);

                User user = new User();
                user.setEmail(email);
                user.setLname(lname);
                user.setFname(fname);
                user.setRole(role);
                user.setPassword(password_hash_salting);
                user.setSalt(salt);
                
                userRepository.save(user);

                return user;
            }
        }

        throw new ResourceNotFoundException("Some thing went wrong!");
    }

    public UserDTO toDTO(User user){
        return userService.toDto(user);
    }


}
