package com.ffmoyano.jwtcrud.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "token")
public class Token implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "id_user")
    @OneToOne(fetch = FetchType.EAGER)
    private AppUser user;
    private String token;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "token_expiration", columnDefinition = "TIMESTAMP")
    private LocalDateTime tokenExpiration;
    @Column(name = "refresh_token_expiration", columnDefinition = "TIMESTAMP")
    private LocalDateTime refreshTokenExpiration;

    @PreUpdate
    @PrePersist
    private void setExpireTime() {
        this.tokenExpiration = LocalDateTime.now().plusMinutes(30);
        this.refreshTokenExpiration = LocalDateTime.now().plusDays(30);
    }

    public Token() {

    }

    public Token(AppUser user, String token, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(LocalDateTime tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public LocalDateTime getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public void setRefreshTokenExpiration(LocalDateTime refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return id.equals(token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
