package com.ffmoyano.idunn.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ffmoyano.idunn.configuration.AppPropertiesConfiguration;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final Logger log;

    private final AppPropertiesConfiguration appPropertiesConfiguration;

    public CustomAuthorizationFilter(Logger log, AppPropertiesConfiguration appPropertiesConfiguration) {
        this.log = log;
        this.appPropertiesConfiguration = appPropertiesConfiguration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // if in login or signup page or refresh token request don't trigger this
        if (request.getServletPath().equals("/")
                || request.getServletPath().equals("/login")
                || request.getServletPath().equals("/signup")) {
            chain.doFilter(request, response);
        } else {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    DecodedJWT decodedJWT = verifyAndDecodeJwt(authHeader);
                    UsernamePasswordAuthenticationToken authToken = getAuthorizationToken(decodedJWT);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    chain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    var error =
                            Map.of("Error:", "Expired Token", "Expired:", true);
                    generateErrorResponse(e, response, error);
                } catch (Exception e) {
                    var error = Map.of("Error:", e);
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

    private DecodedJWT verifyAndDecodeJwt(String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    private void generateErrorResponse(Exception e, HttpServletResponse response, Map error) throws IOException {
        log.error("Error procesando el token JWT en el filtro de autorizacion: ", e);
        response.setHeader("Error", e.getMessage());
        response.setStatus(UNAUTHORIZED.value());
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
