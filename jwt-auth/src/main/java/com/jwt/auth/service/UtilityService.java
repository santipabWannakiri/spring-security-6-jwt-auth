package com.jwt.auth.service;

import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.model.json.response.JwtTokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public interface UtilityService {

    public ResponseEntity<JsonResponse> entityResponseMessage(HttpStatusCode status, JsonResponse response);

    public ResponseEntity<JwtTokenResponse> entityJwtTokenResponseMessage(HttpStatusCode status, JwtTokenResponse response);

    public void servletResponseMessage(HttpServletResponse response, int status, JsonResponse msgResponse) throws IOException;

    public AntPathRequestMatcher[] getAntMathers(String[] paths);
}
