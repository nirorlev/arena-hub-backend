package com.threeatom.common.shiro;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@RequiredArgsConstructor
@Getter
public class BearerToken implements AuthenticationToken {

    private final String token;
    private final String type;

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
