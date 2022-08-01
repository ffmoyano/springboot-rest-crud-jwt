package com.ffmoyano.jwtcrud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/authorized")
    public String authorized() {
        return "hello authorized";
    }

}
