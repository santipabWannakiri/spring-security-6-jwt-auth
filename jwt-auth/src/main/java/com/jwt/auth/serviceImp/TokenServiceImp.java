package com.jwt.auth.serviceImp;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jwt.auth.model.Token;
import com.jwt.auth.repository.TokenRepository;
import com.jwt.auth.service.TokenService;
import com.jwt.auth.service.UtilityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenServiceImp implements TokenService {

    private UtilityService utilityService;
    private TokenRepository tokenRepository;

    @Autowired
    public TokenServiceImp(UtilityService utilityService, TokenRepository tokenRepository) {
        this.utilityService = utilityService;
        this.tokenRepository = tokenRepository;
    }

    @Value("${jwt-auth.secretKey}")
    private String secretKey;
    @Value("${jwt-auth.issuer}")
    private String issuer;
    @Value("${jwt-auth.audience}")
    private String audience;
    @Value("${jwt-auth.accessTokenExpirationTimeMillis}")
    private long accessTokenExpirationTimeMillis;
    @Value("${jwt-auth.refreshTTokenExpirationTimeMillis}")
    private long refreshTokenExpirationTimeMillis;

    @Override
    public Map<String, Object> generateToken(String userName, List<String> role) {
        Map<String, Object> tokenObject = new HashMap<>();
        String token = null;
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + accessTokenExpirationTimeMillis);

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
    public Map<String, Object> generateRefreshToken() {
        String refreshToken = RandomStringUtils.randomAlphanumeric(64);
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + refreshTokenExpirationTimeMillis);

        Map<String, Object> refreshTokenObject = new HashMap<>();
        refreshTokenObject.put("currentTime", new Date(currentTimeMillis));
        refreshTokenObject.put("expiresIn", expirationDate);
        refreshTokenObject.put("refreshToken", refreshToken);
        return refreshTokenObject;
    }

    @Override
    public Token recordToken(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public Token findRefreshToken(String refreshToken) {
        return tokenRepository.findByTokenValue(refreshToken);
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
    public boolean validateAccessToken(String token) {
        try {
            //Verify the integrity token
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            //Get expire date
            Date expirationDate = jwt.getExpiresAt();
            if (isTokenExpired(expirationDate)) {
                //Token has expired or Invalid-token
                return false;
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
