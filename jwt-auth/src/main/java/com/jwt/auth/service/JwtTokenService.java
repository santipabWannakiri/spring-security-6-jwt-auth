package com.jwt.auth.service;



import java.util.List;
import java.util.Map;


public interface JwtTokenService {

    public Map<String,Object> generateToken(String userName, List<String> role);


}
