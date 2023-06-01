package com.jwt.auth.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/management")
public class ManagementController {


    @GetMapping("/g")
    @ResponseBody
    public String managementGet() {

        return "Hello from GET";
    }

    @PostMapping("/p")
    @ResponseBody
    public String managementPost() {

        return "Hello from POST";
    }

    @DeleteMapping("/d")
    @ResponseBody
    public String managementDelete() {

        return "Hello from DELETE";
    }

}
