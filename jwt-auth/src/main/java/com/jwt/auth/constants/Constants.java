package com.jwt.auth.constants;

public final class Constants {

    //SUCCESS
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE_CODE = "SUCCESS";
    //INVALID_FORMAT
    public static final String INVALID_FORMAT_ERROR_CODE = "6400";
    public static final String INVALID_FORMAT_MESSAGE_CODE = "INVALID_FORMAT";
    //INTERNAL_SERVER_ERROR
    public static final String INTERNAL_ERROR_CODE = "6601";
    public static final String INTERNAL_MESSAGE_CODE = "FAILURE";
    //USER_NOT_FOUND
    public static final String USER_NOT_FOUND_ERROR_CODE = "6600";
    public static final String USER_NOT_FOUND_MESSAGE_CODE = "USER_NOT_FOUND";
    public static final String TOKEN_NOT_FOUND_ERROR_CODE = "6600";
    public static final String TOKEN_NOT_FOUND_MESSAGE_CODE = "TOKEN_NOT_FOUND";

    public static final String TOKEN_EXPIRE_ERROR_CODE = "6600";
    public static final String TOKEN_EXPIRE_MESSAGE_CODE = "TOKEN_EXPIRED";

    //====== Description Message ========
    public static final String REGISTER_SUCCESS_MESSAGE = "Registered successfully.";
    public static final String UNABLE_REGISTER_MESSAGE = "Unable to register. Please check your connection and try again.";
    public static final String UNABLE_EXTRACT_TOKEN_MESSAGE = "Authorization header not found in the request.";
    public static final String INVALID_OR_EXPIRE_MESSAGE = "Invalid token or token has expired.";
    public static final String UNABLE_TO_PROCESS_MESSAGE = "Unable to process request. Please try again.";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    public static final String REFRESH_TOKEN_NOT_FOUND_MESSAGE = "Refresh token not found.";
    public static final String UNABLE_CREATE_TOKEN_MESSAGE = "Failed to create JWT token.";
    public static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "Refresh token expired.";


}
