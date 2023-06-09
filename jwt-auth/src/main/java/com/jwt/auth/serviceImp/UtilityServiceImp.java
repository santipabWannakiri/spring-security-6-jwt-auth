package com.jwt.auth.serviceImp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.auth.model.ReponseObject;
import com.jwt.auth.service.UtilityService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Service
public class UtilityServiceImp implements UtilityService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<String> generateResponseMessage(HttpStatusCode status, String code, String msgCode, String reason) {
        String response = "{\r\n" + "    \"code\": \"" + code + "\",\r\n" + "    \"msgCode\": \"" + msgCode + "\",\r\n" + "    \"reason\": \"" + reason + "\"\r\n" + "}";

        return ResponseEntity.status(status).body(response);
    }

    @Override
    public ResponseEntity<String> generateJwtTokenResponseMessage(HttpStatusCode status, String code, String msgCode, String token, Date expire) {
        String response = "{\r\n" + "    \"code\": \"" + code + "\",\r\n" + "    \"msgCode\": \"" + msgCode + "\",\r\n" + "\"data\": {" + "\"token\": \"" + token + "\",\r\n" + "\"expiresIn\": \"" + expire + "\"\r\n" + "}" + "}";
        return ResponseEntity.status(status).body(response);
    }

    @Override
    public void sendErrorJson(HttpServletResponse response, int status, ReponseObject msgResponse) throws IOException {
        ReponseObject reponseObject = new ReponseObject(msgResponse);

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(reponseObject));
       // response.flushBuffer();
    }


}
