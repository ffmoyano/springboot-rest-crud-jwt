package com.ffmoyano.jwtcrud.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffmoyano.jwtcrud.configuration.AppPropertiesConfiguration;
import com.ffmoyano.jwtcrud.service.TokenService;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final Logger log;
    private final TokenService tokenService;

    public CustomAuthorizationFilter(Logger log, AppPropertiesConfiguration appPropertiesConfiguration, TokenService tokenService) {
        this.log = log;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/")
                || request.getServletPath().equals("/login")) {
            chain.doFilter(request, response);
        } else {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    DecodedJWT decodedJWT = tokenService.verifyAndDecodeJwt(authHeader);
                    UsernamePasswordAuthenticationToken authToken = getAuthorizationToken(decodedJWT);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    chain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    // return 401 with the refresh token and then te client asks for a new token
                    String refreshToken = request.getParameter("RefreshToken");
                    var error =
                            Map.of("Error", "Expired Token","RefreshToken", refreshToken);
                    generateErrorResponse(e, response, error);
                } catch (Exception e) {
                    var error = Map.of("Error:", e.getMessage());
                    generateErrorResponse(e, response, error);
                }
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    private UsernamePasswordAuthenticationToken getAuthorizationToken(DecodedJWT jwt) {
        String username = jwt.getSubject();
        String[] roles = jwt.getClaim("roles").asArray(String.class);
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        Arrays.stream(roles).forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }



    private void generateErrorResponse(Exception e, HttpServletResponse response, Map<String, String> error) throws IOException {
        log.error("Error procesando el token JWT en el filtro de autorizacion: ", e);
        response.setHeader("Error", e.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
