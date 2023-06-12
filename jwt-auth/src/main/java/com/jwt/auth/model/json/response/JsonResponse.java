package com.jwt.auth.model.json.response;

import lombok.Data;

@Data
public class JsonResponse {

    private String errorCode;
    private String messageCode;
    private String message;

    public JsonResponse(String errorCode, String messageCode, String message) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.message = message;
    }

    public JsonResponse(JsonResponse msgResponse) {
        this.errorCode = msgResponse.errorCode;
        this.messageCode = msgResponse.messageCode;
        this.message = msgResponse.message;
    }
}
