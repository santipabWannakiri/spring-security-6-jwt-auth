package com.jwt.auth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCredentials {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;

}
