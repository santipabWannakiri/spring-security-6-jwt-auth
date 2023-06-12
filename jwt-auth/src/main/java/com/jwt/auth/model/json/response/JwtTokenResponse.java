package com.jwt.auth.model.json.response;

import lombok.Data;

@Data
public class JwtTokenResponse {
    private String errorCode;
    private String messageCode;
    private JwtData jwtData;

    public JwtTokenResponse(String errorCode, String messageCode, JwtData jwtData) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.jwtData = jwtData;
    }
}
