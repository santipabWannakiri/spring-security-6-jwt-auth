package com.jwt.auth.service;


import com.jwt.auth.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface TokenService {

    public Map<String, Object> generateToken(String userName, List<String> role);

    public Map<String, Object> generateRefreshToken();

    public Token recordToken(Token token);

    public Token findRefreshToken(String refreshToken);

    public String extractTokenFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;

    public String getUsernameFromToken(String token);

    public boolean validateAccessToken(String token);

    public boolean isTokenExpired(Date expirationDate);
}
