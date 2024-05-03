//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.shiro.filter;

import com.threeatom.common.exception.ShiroGlobalExceptionUtil;
import com.threeatom.common.jwt.JwtToken;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeappJwtFilter extends BasicHttpAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeappJwtFilter.class);

    public WeappJwtFilter() {}

    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        LOGGER.info("isLoginAttempt");
        return true;
    }

    protected boolean isAccessAllowed(
            ServletRequest request, ServletResponse response, Object mappedValue) {
        String auth = this.getAuthzHeader(request);
        if (auth != null && !auth.equals("")) {
            try {
                JwtToken token = new JwtToken(auth, "weapp");
                this.getSubject(request, response).login(token);
                LOGGER.info("验证通过");
                return true;
            } catch (Exception var6) {
                ShiroGlobalExceptionUtil.exceptionHandler(var6, response);
                return false;
            }
        } else {
            ShiroGlobalExceptionUtil.exceptionHandler(new AuthenticationException("没有TOKEN"), response);
            return false;
        }
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        return false;
    }
}
