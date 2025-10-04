package com.softdev.softdev.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDTO {
    @NotNull(message="email is required")
    private String email;

    @NotNull(message="password is required")
    private String password;
}
