package com.ffmoyano.jwtcrud.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ffmoyano.jwtcrud.configuration.AppPropertiesConfiguration;
import com.ffmoyano.jwtcrud.dto.TokenDto;
import com.ffmoyano.jwtcrud.entity.Token;
import com.ffmoyano.jwtcrud.entity.AppUser;
import com.ffmoyano.jwtcrud.repository.TokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    private final AppPropertiesConfiguration appPropertiesConfiguration;

    public TokenService(TokenRepository tokenRepository, AppPropertiesConfiguration appPropertiesConfiguration) {
        this.tokenRepository = tokenRepository;
        this.appPropertiesConfiguration = appPropertiesConfiguration;
    }

    public TokenDto generateTokens(User user, String url) {
        String jwtToken = generateJwt(user.getUsername(), user.getAuthorities(), url);
        String refreshToken = generateRefreshToken();
        return new TokenDto(jwtToken, refreshToken);
    }


    private String generateJwt(String email, Collection<GrantedAuthority> authorities, String url) {

        Algorithm algorithm = Algorithm.HMAC256(appPropertiesConfiguration.getJwtSecret().getBytes(StandardCharsets.UTF_8));

        return JWT.create()
                .withSubject(email)
                // 30 minutes
                .withExpiresAt(new Date(System.currentTimeMillis() + (30 * 60 * 1000)))
                .withIssuer(url)
                .withClaim("roles", authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
    }

    private String generateRefreshToken() {
        return RandomStringUtils.randomAlphanumeric(26);
    }


    public Token save(Token token) {
        return tokenRepository.save(token);
    }


    public Token findTokenByUser(AppUser user) {
        return tokenRepository.findTokenByUser(user);
    }


}
