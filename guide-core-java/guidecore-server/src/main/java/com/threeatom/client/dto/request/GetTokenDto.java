package com.threeatom.client.dto.request;

import feign.form.FormProperty;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class GetTokenDto {
    @FormProperty("client_id")
    private List<String> clientId;
    @FormProperty("client_secret")
    private List<String> clientSecret;
    @FormProperty("grant_type")
    private List<String> grantType;
    @FormProperty("redirect_uri")
    private List<String> redirectUri;
    @FormProperty("code")
    private List<String> code;
    @FormProperty("refresh_token")
    private List<String> refreshToken;

    public void setClientId(String clientId) {
        this.clientId = Collections.singletonList(clientId);
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = Collections.singletonList(clientSecret);
    }

    public void setGrantType(String grantType) {
        this.grantType = Collections.singletonList(grantType);
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = Collections.singletonList(redirectUri);
    }

    public void setCode(String code) {
        this.code = Collections.singletonList(code);
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = Collections.singletonList(refreshToken);
    }
}
