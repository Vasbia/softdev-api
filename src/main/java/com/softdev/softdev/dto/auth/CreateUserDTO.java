package com.softdev.softdev.dto.auth;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserDTO {

    @NotNull(message="fname is required")
    private String fname;

    @NotNull(message="lname is required")
    private String lname;

    @NotNull(message="email is required")
    @Pattern(regexp="^([\\w]*[\\w\\.]*(?!\\.)@.*)", message="Invalid email format")
    private String email;   

    @NotNull(message="password is required")
    private String password;

    @NotNull(message="role is required")
    private String role;

}
