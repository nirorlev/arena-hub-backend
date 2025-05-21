package com.threeatom.guidecore.shiro;

import com.threeatom.common.exception.ShiroGlobalExceptionUtil;
import com.threeatom.common.jwt.JwtToken;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuideCoreJwtFilter extends BasicHttpAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuideCoreJwtFilter.class);

    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        LOGGER.info("isLoginAttempt");

        return true;
    }

    @Override
    protected boolean isAccessAllowed(
            ServletRequest request, ServletResponse response, Object mappedValue) {
        String auth = getAuthzHeader(request);
        if (auth != null && !auth.equals("")) {

            try {
                JwtToken token = new JwtToken(auth, "guidecore");
                // 检查认证
                getSubject(request, response).login(token);

                return true;

            } catch (Exception e) {
                ShiroGlobalExceptionUtil.exceptionHandler(e, response);
                LOGGER.error("验证异常", e.getCause());
                return false;
            }

        } else {
            // 没有TOKEN 不能往下访问
            ShiroGlobalExceptionUtil.exceptionHandler(new AuthenticationException("没有TOKEN"), response);
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
            throws Exception {
        return false;
    }
}
