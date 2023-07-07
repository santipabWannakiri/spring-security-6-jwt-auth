package com.jwt.auth.exception.type;

public class InternalErrorException extends RuntimeException{
    public InternalErrorException(String message){
        super(message);
    }
}
