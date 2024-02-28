package com.ffmoyano.jwtcrud.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ffmoyano.jwtcrud.configuration.AppPropertiesConfiguration;
import com.ffmoyano.jwtcrud.dto.TokenDto;
import com.ffmoyano.jwtcrud.entity.Role;
import com.ffmoyano.jwtcrud.entity.Token;
import com.ffmoyano.jwtcrud.entity.AppUser;
import com.ffmoyano.jwtcrud.repository.TokenRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    private final AppPropertiesConfiguration appPropertiesConfiguration;
    private final UserService userService;

    public TokenService(TokenRepository tokenRepository, AppPropertiesConfiguration appPropertiesConfiguration, UserService userService) {
        this.tokenRepository = tokenRepository;
        this.appPropertiesConfiguration = appPropertiesConfiguration;
        this.userService = userService;
    }

    public TokenDto generateTokens(User user) {
        String jwtToken = generateJwt(user.getUsername(), user.getAuthorities());
        String refreshToken = generateRefreshToken(user.getUsername());
        return new TokenDto(jwtToken, refreshToken);
    }

    public TokenDto generateTokens(AppUser user) {
        String jwtToken = generateJwt(user.getEmail(), user.getRoles());
        String refreshToken = generateRefreshToken(user.getEmail());
        return new TokenDto(jwtToken, refreshToken);
    }

    public TokenDto refreshTokens(String refreshToken) {
        TokenDto tokens = null;
        DecodedJWT decodedJWT = verifyAndDecodeJwt(refreshToken != null ? refreshToken : "");
        if (decodedJWT != null) {
            AppUser user = userService.findByEmail(decodedJWT.getSubject());
            tokens = generateTokens(user);
        }
        return tokens;
    }


    private String generateJwt(String email, Collection<GrantedAuthority> authorities) {

        Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(email)
                // 30 minutes
                .withExpiresAt(new Date(System.currentTimeMillis() + (30 * 60 * 1000)))
                .withIssuer("SpringSkel") // replace it, preferably in properties
                .withClaim("roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    private String generateJwt(String email, List<Role> roles) {

        Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(email)
                // 30 minutes
                .withExpiresAt(new Date(System.currentTimeMillis() + (30 * 60 * 1000)))
                .withIssuer("SpringSkel") // replace it, preferably in properties
                .withClaim("roles", roles.stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    private String generateRefreshToken(String email) {
        Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(email)
                // 24 hours
                .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
                .withIssuer("SpringSkel") // replace it, preferably in properties
                .sign(algorithm);
    }


    public Token save(Token token) {
        return tokenRepository.save(token);
    }


    public Token findTokenByUser(AppUser user) {
        return tokenRepository.findTokenByUser(user);
    }

    public DecodedJWT verifyAndDecodeJwt(String authHeader) {
        try {
            String token = authHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (Exception e) {
            // log
            return null;
        }

    }
}
