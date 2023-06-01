package com.jwt.auth.controller;


import com.jwt.auth.model.User;
import com.jwt.auth.service.UserService;
import com.jwt.auth.service.UtilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private UtilityService utilityService;

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
}