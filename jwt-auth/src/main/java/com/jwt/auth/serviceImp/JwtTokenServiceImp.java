package com.jwt.auth.serviceImp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.jwt.auth.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtTokenServiceImp implements JwtTokenService {
    @Value("${jwt-auth.secretKey}")
    private String secretKey;
    @Value("${jwt-auth.issuer}")
    private String issuer;
    @Value("${jwt-auth.audience}")
    private String audience;
    @Value("${jwt-auth.expirationTimeMillis}")
    private long expirationTimeMillis;


    @Override
    public String generateToken(String userName, String role) {
        String token = null;
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + expirationTimeMillis);

        // Set the header values
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            token = JWT.create()
                    .withHeader(header)
                    .withIssuer(issuer)
                    .withSubject(userName)
                    .withClaim("role", role) //custom claim
                    .withAudience(audience)
                    .withIssuedAt(new Date(currentTimeMillis))
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        } catch (JWTCreationException exception) {
            System.out.println(exception.getMessage());
        }
        return token;
    }


}
