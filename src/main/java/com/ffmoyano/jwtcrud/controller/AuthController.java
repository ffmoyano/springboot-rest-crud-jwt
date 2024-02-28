package com.ffmoyano.jwtcrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffmoyano.jwtcrud.dto.TokenDto;
import com.ffmoyano.jwtcrud.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/authorized")
    public String authorized() {
        return "hello authorized";
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String refreshToken = request.getHeader("RefreshToken");
        TokenDto tokens = tokenService.refreshTokens(refreshToken);
        if (tokens != null) {
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}
