package com.jwt.auth.exception.type;

public class UserDuplicateException extends RuntimeException{
    public UserDuplicateException(String message) {
        super(message);
    }

}
