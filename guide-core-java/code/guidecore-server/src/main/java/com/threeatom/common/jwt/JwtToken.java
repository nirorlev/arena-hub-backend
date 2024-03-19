package com.threeatom.common.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Getter
@RequiredArgsConstructor
public class JwtToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;
    private final String token;
    private final String type;

    public Object getPrincipal() {
        return this.token;
    }

    public Object getCredentials() {
        return this.token;
    }
}
