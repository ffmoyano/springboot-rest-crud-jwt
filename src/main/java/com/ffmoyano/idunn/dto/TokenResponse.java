package com.ffmoyano.idunn.dto;

public class TokenResponse {

    private String authToken;

    private String refreshToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public TokenResponse(String authToken, String refreshToken) {
        this.authToken = authToken;
        this.refreshToken = refreshToken;
    }
}
