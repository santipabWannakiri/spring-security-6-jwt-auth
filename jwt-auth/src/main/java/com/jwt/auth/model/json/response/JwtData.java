package com.jwt.auth.model.json.response;

import lombok.Data;

import java.util.Date;

@Data
public class JwtData {
    private String token;
    private Date expiresIn;

    public JwtData(String token, Date expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
