package com.tolmic.api.hh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2TokenConfig {

    @Value("")
    private String accessToken;

    @Value("")
    private String refreshToken;

    public OAuth2Token CurrentOAuth2Token() {
        OAuth2Token currentOAuth2Token = new OAuth2Token(null, null);

        return currentOAuth2Token;
    }

}
