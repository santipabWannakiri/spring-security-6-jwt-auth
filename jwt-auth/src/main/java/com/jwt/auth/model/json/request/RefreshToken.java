package com.jwt.auth.model.json.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshToken {
    @NotBlank(message = "is mandatory")
    private String refreshToken;

    public RefreshToken() {
    }

    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
