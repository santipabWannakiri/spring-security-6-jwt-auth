package com.jwt.auth.model;

import lombok.Data;

@Data
public class ReponseObject {

    private String errorCode;
    private String messageCode;
    private String message;

    public ReponseObject(String errorCode, String messageCode, String message) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.message = message;
    }

    public ReponseObject(ReponseObject msgResponse) {
        this.errorCode = msgResponse.errorCode;
        this.messageCode = msgResponse.messageCode;
        this.message = msgResponse.message;
    }
}
