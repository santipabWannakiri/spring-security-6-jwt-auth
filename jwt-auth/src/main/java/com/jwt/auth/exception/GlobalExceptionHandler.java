package com.jwt.auth.exception;

import com.jwt.auth.exception.type.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.jwt.auth.constants.Constants.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorJsonResponse> handleException(Exception ex) {
        ErrorJsonResponse errorJsonResponse = new ErrorJsonResponse();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof InvalidFormatException) {
            errorJsonResponse.setAppResponseCode(INVALID_FORMAT_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(INVALID_FORMAT_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UserDuplicateException) {
            errorJsonResponse.setAppResponseCode(USER_DUPLICATE_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(USER_DUPLICATE_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.CONFLICT;
        } else if (ex instanceof UserNotFoundException) {
            errorJsonResponse.setAppResponseCode(USER_NOT_FOUND_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(USER_NOT_FOUND_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof IncorrectPasswordException) {
            errorJsonResponse.setAppResponseCode(PASSWORD_INCORRECT_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(PASSWORD_INCORRECT_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof TokenNotFoundException) {
            errorJsonResponse.setAppResponseCode(TOKEN_NOT_FOUND_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(TOKEN_NOT_FOUND_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof TokenExpiredException) {
            errorJsonResponse.setAppResponseCode(TOKEN_EXPIRE_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(TOKEN_EXPIRE_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof InternalErrorException) {
            errorJsonResponse.setAppResponseCode(INTERNAL_ERROR_CODE);
            errorJsonResponse.setAppMessageCode(INTERNAL_MESSAGE_CODE);
            errorJsonResponse.setDescription(ex.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(errorJsonResponse, httpStatus);
    }

}
