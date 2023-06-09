package com.jwt.auth.service;

import com.jwt.auth.model.ReponseObject;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Date;

public interface UtilityService {

    public ResponseEntity<String> generateResponseMessage(HttpStatusCode status, String code, String msgCode, String reason);

    public ResponseEntity<String> generateJwtTokenResponseMessage(HttpStatusCode status, String code, String msgCode, String token, Date expire);

    public void sendErrorJson(HttpServletResponse response, int status, ReponseObject msgResponse) throws IOException;
}
