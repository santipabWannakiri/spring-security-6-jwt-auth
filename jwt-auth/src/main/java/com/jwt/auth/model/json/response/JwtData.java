package com.jwt.auth.model.json.response;

import lombok.Data;

import java.util.Date;

@Data
public class JwtData {
    private String token;
    private Date expiresIn;
    private String refreshToken;

    public JwtData() {
    }

    public JwtData(String token, String refreshToken, Date expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
    }
}
