package com.jwt.auth.service;

import java.security.interfaces.ECKey;
import java.util.Map;

public interface JwtTokenService {

    public String generateToken(String userName, String role);


}
