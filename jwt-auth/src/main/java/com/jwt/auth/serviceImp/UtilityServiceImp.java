package com.jwt.auth.serviceImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.model.json.response.JwtTokenResponse;
import com.jwt.auth.service.UtilityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UtilityServiceImp implements UtilityService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<JsonResponse> entityResponseMessage(HttpStatusCode status, JsonResponse response) {
        return ResponseEntity.status(status).body(response);
    }


    @Override
    public ResponseEntity<JwtTokenResponse> entityJwtTokenResponseMessage(HttpStatusCode status, JwtTokenResponse response) {
        return ResponseEntity.status(status).body(response);
    }

    @Override
    public void servletResponseMessage(HttpServletResponse response, int status, JsonResponse msgResponse) throws IOException {
        JsonResponse responseObject = new JsonResponse(msgResponse);

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseObject));
        response.flushBuffer();
    }

    @Override
    public AntPathRequestMatcher[] getAntMathers(String[] paths) {
        AntPathRequestMatcher[] matcher = new AntPathRequestMatcher[paths.length];
        for (int i = 0; i < paths.length; i++) {
            matcher[i] = new AntPathRequestMatcher(paths[i]);
        }
        return matcher;
    }


}
