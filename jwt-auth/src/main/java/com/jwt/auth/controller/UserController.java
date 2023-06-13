package com.jwt.auth.controller;


import com.jwt.auth.model.*;
import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.model.json.response.JwtData;
import com.jwt.auth.model.json.response.JwtTokenResponse;
import com.jwt.auth.service.JwtTokenService;
import com.jwt.auth.service.UserService;
import com.jwt.auth.service.UtilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.jwt.auth.constants.ErrorConstants.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private JwtTokenService jwtTokenService;


    @GetMapping("/public")
    @ResponseBody
    public String anyone() {

        return "Hello, anonymously !";
    }

    @PostMapping(value = "/reg", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonResponse> registerUser(@RequestBody @Valid User userInfo, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return utilityService.entityResponseMessage(HttpStatus.BAD_REQUEST, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, errorMessages.get(0)));
        } else {
            User existingUser = userService.findByUser(userInfo.getUsername());
            if (existingUser != null) {
                return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(USER_NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_MESSAGE_CODE, USER_NOT_FOUND_MESSAGE));
            } else {
                User createdUser = userService.createUser(userInfo);
                if (createdUser != null) {
                    return utilityService.entityResponseMessage(HttpStatus.OK, new JsonResponse(SUCCESS_CODE, SUCCESS_MESSAGE_CODE, REGISTER_SUCCESS_MESSAGE));
                } else {
                    return utilityService.entityResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_REGISTER_MESSAGE));
                }
            }
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody @Valid UserCredentials credentials, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());
            return utilityService.entityResponseMessage(HttpStatus.BAD_REQUEST, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, errorMessages.get(0)));

        } else {
            User existingUser = userService.findByUser(credentials.getUsername());
            if (existingUser == null) {
                return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(USER_NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_MESSAGE_CODE, USER_NOT_FOUND_MESSAGE));
            } else {
                List<String> roleList = new ArrayList<>();
                for (Role role : existingUser.getRoles()) {
                    roleList.add(role.getName());
                }
                Map<String, Object> accessTokenObject = jwtTokenService.generateToken(existingUser.getUsername(), roleList);
                Map<String, Object> refreshTokenObject = jwtTokenService.generateRefreshToken();
                if (accessTokenObject == null) {
                    return utilityService.entityResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_CREATE_TOKEN_MESSAGE));
                }
                Token refreshToken = jwtTokenService.recordToken(new Token((String) refreshTokenObject.get("refreshToken"), TokenType.REFRESH_TOKEN, TokenStatus.ACTIVE, (Date) refreshTokenObject.get("expiresIn"), (Date) refreshTokenObject.get("currentTime"), existingUser));
                if (refreshToken == null) {
                    return utilityService.entityResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_CREATE_TOKEN_MESSAGE));
                }
                return utilityService.entityJwtTokenResponseMessage(HttpStatus.OK, new JwtTokenResponse(SUCCESS_CODE, SUCCESS_MESSAGE_CODE, new JwtData((String) accessTokenObject.get("token"), refreshToken.getTokenValue(), (Date) accessTokenObject.get("expiresIn"))));
            }
        }


    }

}