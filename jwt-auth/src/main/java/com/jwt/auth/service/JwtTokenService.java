package com.jwt.auth.service;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface JwtTokenService {

    public Map<String,Object> generateToken(String userName, List<String> role);

    public String extractTokenFromRequest (HttpServletRequest request, HttpServletResponse response) throws IOException;

    public String getUsernameFromToken(String token);

    public boolean validateToken(String token);

    public  boolean isTokenExpired (Date expirationDate);
}
