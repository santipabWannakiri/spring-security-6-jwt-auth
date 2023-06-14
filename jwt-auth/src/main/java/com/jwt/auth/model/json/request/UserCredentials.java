package com.jwt.auth.model.json.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCredentials {

    @NotBlank(message = "is mandatory")
    private String username;

    @NotBlank(message = "is mandatory")
    private String password;

}
