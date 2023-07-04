package com.jwt.auth.controller;


import com.jwt.auth.model.*;
import com.jwt.auth.model.json.request.RefreshToken;
import com.jwt.auth.model.json.request.UserCredentials;
import com.jwt.auth.model.json.response.JsonResponse;
import com.jwt.auth.model.json.response.JwtData;
import com.jwt.auth.model.json.response.JwtTokenResponse;
import com.jwt.auth.service.TokenService;
import com.jwt.auth.service.UserService;
import com.jwt.auth.service.UtilityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.jwt.auth.constants.Constants.*;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private UserService userService;
    private UtilityService utilityService;
    private TokenService tokenService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, UtilityService utilityService, TokenService tokenService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.utilityService = utilityService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/public")
    @ResponseBody
    public String anyone() {
        return "Hello, anonymously !";
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonResponse> registerUser(@RequestBody @Valid User userInfo, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
            return utilityService.entityResponseMessage(HttpStatus.BAD_REQUEST, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, errorMessages.get(0)));
        } else {
            User existingUser = userService.findByUser(userInfo.getUsername());
            if (existingUser != null) {
                return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(USER_DUPLICATE_ERROR_CODE, USER_DUPLICATE_MESSAGE_CODE, USER_DUPLICATE_MESSAGE));
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
            log.error("/login-->INVALID_FORMAT  ( \""+errorMessages.get(0)+"\")");
            return utilityService.entityResponseMessage(HttpStatus.BAD_REQUEST, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, errorMessages.get(0)));
        }
        User existingUser = userService.findByUser(credentials.getUsername());
        if (existingUser == null) {
            log.error("/login-->USER_NOT_FOUND  (User: \""+credentials.getUsername()+"\" not found)");
            return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(USER_NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_MESSAGE_CODE, USER_NOT_FOUND_MESSAGE));
        }
        if (!passwordEncoder.matches(credentials.getPassword(), existingUser.getPassword())) {
            log.error("/login-->PASSWORD_INCORRECT  (User: \""+credentials.getUsername()+"\" password incorrect)");
            return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(PASSWORD_INCORRECT_ERROR_CODE, PASSWORD_INCORRECT_MESSAGE_CODE, PASSWORD_INCORRECT_MESSAGE));
        }
        return processUser(existingUser);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshToken reqRefreshToken, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());
            return utilityService.entityResponseMessage(HttpStatus.BAD_REQUEST, new JsonResponse(INVALID_FORMAT_ERROR_CODE, INVALID_FORMAT_MESSAGE_CODE, errorMessages.get(0)));
        }
        Token refreshToken = tokenService.findRefreshToken(reqRefreshToken.getRefreshToken());
        if (refreshToken == null) {
            return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(TOKEN_NOT_FOUND_ERROR_CODE, TOKEN_NOT_FOUND_MESSAGE_CODE, REFRESH_TOKEN_NOT_FOUND_MESSAGE));
        }
        if (tokenService.isTokenExpired(refreshToken.getExpirationDate())) {
            return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(TOKEN_EXPIRE_ERROR_CODE, TOKEN_EXPIRE_MESSAGE_CODE, REFRESH_TOKEN_EXPIRED_MESSAGE));
        }
        Optional<User> existingUser = userService.findById(refreshToken.getUser().getId());
        if (!existingUser.isPresent()) {
            return utilityService.entityResponseMessage(HttpStatus.CONFLICT, new JsonResponse(USER_NOT_FOUND_ERROR_CODE, USER_NOT_FOUND_MESSAGE_CODE, USER_NOT_FOUND_MESSAGE));
        }
        return processUser(existingUser.get());
    }

    private ResponseEntity<?> processUser(User user) {
        List<String> roleList = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        Map<String, Object> accessTokenObject = tokenService.generateToken(user.getUsername(), roleList);
        if (accessTokenObject == null) {
            return utilityService.entityResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_CREATE_TOKEN_MESSAGE));
        }
        Map<String, Object> refreshTokenObject = tokenService.generateRefreshToken();
        Token refreshToken = tokenService.recordToken(new Token((String) refreshTokenObject.get("refreshToken"), TokenType.REFRESH_TOKEN, TokenStatus.ACTIVE, (Date) refreshTokenObject.get("expiresIn"), (Date) refreshTokenObject.get("currentTime"), user));
        if (refreshToken == null) {
            return utilityService.entityResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, new JsonResponse(INTERNAL_ERROR_CODE, INTERNAL_MESSAGE_CODE, UNABLE_CREATE_TOKEN_MESSAGE));
        }
        JwtData jwtData = new JwtData((String) accessTokenObject.get("token"), refreshToken.getTokenValue(), (Date) accessTokenObject.get("expiresIn"));
        return utilityService.entityJwtTokenResponseMessage(HttpStatus.OK, new JwtTokenResponse(SUCCESS_CODE, SUCCESS_MESSAGE_CODE, jwtData));
    }
}
