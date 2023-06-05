package com.jwt.auth.controller;


import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.model.UserCredentials;
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
    public ResponseEntity<String> registerUser(@RequestBody @Valid User userInfo, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(utilityService.normalMessageMapping("0300", "INVALID_FORMAT", errorMessages.get(0)));
        } else {
            User existingUser = userService.findByUser(userInfo.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(utilityService.normalMessageMapping("6600", "DUPLICATE", "Username already registered"));
            } else {
                User createdUser = userService.createUser(userInfo);
                if (createdUser != null) {
                    return ResponseEntity.status(HttpStatus.OK).body(utilityService.normalMessageMapping("0100", "SUCCESS", "Registered successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(utilityService.normalMessageMapping("0300", "FAILURE", "Unable to register. Please check your connection and try again."));
                }
            }
        }
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody @Valid UserCredentials credentials, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(error -> error.getField() + " : " + error.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(utilityService.normalMessageMapping("0300", "INVALID_FORMAT", errorMessages.get(0)));
        } else {
            User existingUser = userService.findByUser(credentials.getUsername());
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(utilityService.normalMessageMapping("6600", "NOT_FOUND", "User not found"));
            } else {
                List<String> roleList = new ArrayList<>();
                for (Role role : existingUser.getRoles()) {
                    roleList.add(role.getName());
                }
                Map<String, Object> tokenObject = jwtTokenService.generateToken(existingUser.getUsername(), roleList);
                return ResponseEntity.status(HttpStatus.OK).body(utilityService.jwtResponseMessageMapping("0100", "SUCCESS", (String) tokenObject.get("token"), (Date) tokenObject.get("expiresIn")));
            }
        }


    }

}