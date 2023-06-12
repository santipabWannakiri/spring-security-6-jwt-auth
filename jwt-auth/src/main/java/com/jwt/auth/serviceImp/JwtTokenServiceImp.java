package com.jwt.auth.serviceImp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jwt.auth.service.JwtTokenService;
import com.jwt.auth.service.UtilityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtTokenServiceImp implements JwtTokenService {
    @Autowired
    private UtilityService utilityService;

    @Value("${jwt-auth.secretKey}")
    private String secretKey;
    @Value("${jwt-auth.issuer}")
    private String issuer;
    @Value("${jwt-auth.audience}")
    private String audience;
    @Value("${jwt-auth.expirationTimeMillis}")
    private long expirationTimeMillis;


    @Override
    public Map<String, Object> generateToken(String userName, List<String> role) {
        Map<String, Object> tokenObject = new HashMap<>();
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
            tokenObject = null;
        } catch (JWTCreationException exception) {
            tokenObject = null;
        }
        tokenObject.put("currentTime", new Date(currentTimeMillis));
        tokenObject.put("expiresIn", expirationDate);
        tokenObject.put("token", token);

        return tokenObject;
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            return null;
        }
        return authorizationHeader.substring("Bearer ".length());
    }

    @Override
    public String getUsernameFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);
        return decodedJWT.getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            //Verify the integrity token
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            //Get expire date
            Date expirationDate = jwt.getExpiresAt();
            if (isTokenExpired(expirationDate)) {
                //Token has expired

                return false;   //False(Invalid-token)
            }
        } catch (JWTVerificationException exception) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isTokenExpired(Date expirationDate) {
        Date currentDate = new Date();
        if (expirationDate.before(currentDate)) {
            //Token has expired
            return true;
        }
        return false;
    }


}
