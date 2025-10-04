package com.softdev.softdev.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserDTO {

    @NotNull(message="fname is required")
    private String fname;

    @NotNull(message="lname is required")
    private String lname;

    @NotNull(message="email is required")
    private String email;

    @NotNull(message="password is required")
    private String password;

    @NotNull(message="role is required")
    private String role;

    @NotNull(message="key is required")
    private String key;

}
