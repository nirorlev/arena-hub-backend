package com.threeatom.common.shiro.realm;

import com.threeatom.common.exception.ShiroGlobalExceptionUtil;
import com.threeatom.common.shiro.BearerToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.springframework.beans.factory.annotation.Value;

public class BearerTokenRealm extends AuthenticatingRealm {

    @Value("${admin.api.token}")
    private String adminApiToken;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof BearerToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        BearerToken token = (BearerToken) authenticationToken;

        if (isValidToken(token)) {
            return new SimpleAuthenticationInfo(token, token.getCredentials(), getName());
        }

        throw new AuthenticationException("Invalid admin token");
    }

    private boolean isValidToken(BearerToken token) {
        return adminApiToken.equals(token.getPrincipal());
    }

    @Override
    public String getName() {
        return "Bearer";
    }
}
