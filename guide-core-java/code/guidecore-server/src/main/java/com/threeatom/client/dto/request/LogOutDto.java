package com.threeatom.client.dto.request;

import feign.form.FormProperty;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class LogOutDto {
    @FormProperty("token")
    private List<String> token;
    @FormProperty("client_id")
    private List<String> clientId;

    public void setClientId(String clientId) {
        this.clientId = Collections.singletonList(clientId);
    }

    public void setToken(String token) {
        this.token = Collections.singletonList(token);
    }
}
