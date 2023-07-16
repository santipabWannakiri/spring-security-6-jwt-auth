package com.jwt.auth.model.json.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "Login")
public class UserCredentials {

    @NotBlank(message = "is mandatory")
    private String username;

    @NotBlank(message = "is mandatory")
    private String password;

    public UserCredentials() {
    }

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
