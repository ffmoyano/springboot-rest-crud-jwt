package com.ffmoyano.jwtcrud.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffmoyano.jwtcrud.dto.TokenResponse;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        System.out.println("USERNAME: " + username);
        System.out.println("PASSWORD: " + password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        // retrieve user data from principal and generate tokens
        User user = (User) authentication.getPrincipal();
        String url = request.getRequestURL().toString();
        TokenResponse tokens = tokenService.generateTokens(user, url);
        // retrieve user from username and save tokens to database
        AppUser idunnUser = userService.findByEmail(user.getUsername());
        Token token = tokenService.findTokenByUser(idunnUser);
        if(token == null) {
            token = new Token();
            token.setUser(idunnUser);
        }
        token.setToken(tokens.getAuthToken());
        token.setRefreshToken(tokens.getRefreshToken());

        tokenService.save(token);
        // return jwt and refresh string to
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
