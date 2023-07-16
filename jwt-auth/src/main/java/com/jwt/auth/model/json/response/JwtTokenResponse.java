package com.jwt.auth.model.json.response;

import lombok.Data;

@Data
public class JwtTokenResponse {
    private String appResponseCode;
    private String appMessageCode;
    private JwtData jwtData;

    public JwtTokenResponse() {
    }

    public JwtTokenResponse(String appResponseCode, String appMessageCode, JwtData jwtData) {
        this.appResponseCode = appResponseCode;
        this.appMessageCode = appMessageCode;
        this.jwtData = jwtData;
    }
}
