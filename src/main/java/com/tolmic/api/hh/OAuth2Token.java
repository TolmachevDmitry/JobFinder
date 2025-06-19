package com.tolmic.api.hh;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class OAuth2Token {

    private String accessToken;
    private String refreshToken;

    public OAuth2Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void updateData(OAuth2Token oAuth2Token) {
        this.accessToken = oAuth2Token.getAccessToken();
        this.refreshToken = oAuth2Token.getRefreshToken();
    }

}
