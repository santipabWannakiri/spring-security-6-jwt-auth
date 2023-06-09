package com.jwt.auth.controller;


import com.jwt.auth.exception.type.*;
import com.jwt.auth.model.*;
import com.jwt.auth.model.json.request.RefreshToken;
import com.jwt.auth.model.json.request.UserCredentials;
import com.jwt.auth.model.json.response.GenericResponse;
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
    public ResponseEntity<GenericResponse> registerUser(@RequestBody @Valid User userInfo, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
            log.error("[/signup] --> INVALID_FORMAT  (\""+errorMessages.get(0)+"\")");
            throw new InvalidFormatException(errorMessages.get(0));
        } else {
            User existingUser = userService.findByUser(userInfo.getUsername());
            if (existingUser != null) {
                log.error("[/signup] --> USER_NOT_FOUND  (User: \""+userInfo.getUsername()+"\" duplicated)");
                throw new UserDuplicateException(USER_DUPLICATE_MESSAGE);
            } else {
                User createdUser = userService.createUser(userInfo);
                if (createdUser != null) {
                    log.info("[/signup] --> SUCCESS  (User: \""+userInfo.getUsername()+"\" registered)");
                    return utilityService.responseSuccess(REGISTER_SUCCESS_MESSAGE);
                } else {
                    log.info("[/signup] --> UNABLE_REGISTER  (User: \""+userInfo.getUsername()+"\" unable register)");
                    throw new InternalErrorException(UNABLE_REGISTER_MESSAGE);
                }
            }
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody @Valid UserCredentials credentials, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());
            log.error("[/login] --> INVALID_FORMAT  (\""+errorMessages.get(0)+"\")");
            throw new InvalidFormatException(errorMessages.get(0));
        }
        User existingUser = userService.findByUser(credentials.getUsername());
        if (existingUser == null) {
            log.error("[/login] --> USER_NOT_FOUND  (User: \""+credentials.getUsername()+"\" not found)");
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        if (!passwordEncoder.matches(credentials.getPassword(), existingUser.getPassword())) {
            log.error("[/login] --> PASSWORD_INCORRECT  (User: \""+credentials.getUsername()+"\" password incorrect)");
            throw new IncorrectPasswordException(PASSWORD_INCORRECT_MESSAGE);
        }
        return processUser(existingUser);
    }

    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshToken reqRefreshToken, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());
            throw new InvalidFormatException(errorMessages.get(0));
        }
        Token refreshToken = tokenService.findRefreshToken(reqRefreshToken.getRefreshToken());
        if (refreshToken == null) {
            log.error("[/refresh] --> REFRESH_TOKEN_NOT_FOUND  (Not found refresh token in payload)");
            throw new TokenNotFoundException(REFRESH_TOKEN_NOT_FOUND_MESSAGE);
        }
        if (tokenService.isTokenExpired(refreshToken.getExpirationDate())) {
            log.error("[/refresh] --> REFRESH_TOKEN_EXPIRED  (The token : \""+refreshToken.getExpirationDate()+"\" expired)");
            throw new TokenExpiredException(REFRESH_TOKEN_EXPIRED_MESSAGE);
        }
        Optional<User> existingUser = userService.findById(refreshToken.getUser().getId());
        if (!existingUser.isPresent()) {
            log.error("[/login] --> USER_NOT_FOUND  (User: \""+refreshToken.getUser()+"\" not found)");
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        return processUser(existingUser.get());
    }

    private ResponseEntity<?> processUser(User user) {
        List<String> roleList = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        Map<String, Object> accessTokenObject = tokenService.generateToken(user.getUsername(), roleList);
        if (accessTokenObject == null) {
            log.error("[processUser] --> UNABLE_CREATE_TOKEN  (Unable generate token)");
            throw new InternalErrorException(UNABLE_CREATE_TOKEN_MESSAGE);
        }
        Map<String, Object> refreshTokenObject = tokenService.generateRefreshToken();
        Token refreshToken = tokenService.recordToken(new Token((String) refreshTokenObject.get("refreshToken"), TokenType.REFRESH_TOKEN, TokenStatus.ACTIVE, (Date) refreshTokenObject.get("expiresIn"), (Date) refreshTokenObject.get("currentTime"), user));
        if (refreshToken == null) {
            log.error("[processUser] --> UNABLE_CREATE_REFRESH_TOKEN  (Unable generate refresh token)");
            throw new InternalErrorException(UNABLE_CREATE_TOKEN_MESSAGE);
        }
        JwtData jwtData = new JwtData((String) accessTokenObject.get("token"), refreshToken.getTokenValue(), (Date) accessTokenObject.get("expiresIn"));
        log.info("[processUser] --> SUCCESS_GENERATE_TOKEN  (Success generate token for user : \""+user.getUsername()+"\")");
        return utilityService.entityJwtTokenResponseMessage(HttpStatus.OK, new JwtTokenResponse(SUCCESS_CODE, SUCCESS_MESSAGE_CODE, jwtData));
    }
}
