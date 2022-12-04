package com.ffmoyano.jwtcrud.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffmoyano.jwtcrud.dto.TokenDto;
import com.ffmoyano.jwtcrud.entity.AppUser;
import com.ffmoyano.jwtcrud.entity.Token;
import com.ffmoyano.jwtcrud.service.TokenService;
import com.ffmoyano.jwtcrud.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;



    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService
            , TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException {

        // retrieve user data from principal and generate tokens
        User user = (User) authentication.getPrincipal();
        String url = request.getRequestURL().toString();
        TokenDto tokens = tokenService.generateTokens(user, url);
        // retrieve user from username and save tokens to database
        AppUser appUser = userService.findByEmail(user.getUsername());
        Token token = tokenService.findTokenByUser(appUser);
        if(token == null) {
            token = new Token();
            token.setUser(appUser);
        }
        token.setToken(tokens.getAuthToken());
        token.setRefreshToken(tokens.getRefreshToken());

        tokenService.save(token);
        // return jwt and refresh string to
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
