package com.jwt.auth.serviceImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.model.json.response.JwtTokenResponse;
import com.jwt.auth.service.UtilityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        JsonResponse reponseObject = new JsonResponse(msgResponse);

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(reponseObject));
        // response.flushBuffer();
    }


}
