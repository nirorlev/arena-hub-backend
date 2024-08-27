package com.threeatom.common.shiro.filter;

import com.threeatom.common.exception.ShiroGlobalExceptionUtil;
import com.threeatom.common.shiro.BearerToken;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

public class BearerTokenAuthenticatingFilter extends AuthenticatingFilter {
    @Override
    protected BearerToken createToken(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return new BearerToken(authorizationHeader.substring("Bearer ".length()), "Bearer");
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (executeLogin(request, response)) {
            return true;
        }

        ShiroGlobalExceptionUtil.exceptionHandler(new AuthenticationException("Invalid token"), response);
        return false;
    }
}
