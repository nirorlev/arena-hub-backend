package com.threeatom.guidecore.shiro;

import com.threeatom.common.exception.ShiroGlobalExceptionUtil;
import com.threeatom.common.jwt.JwtToken;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Weapp端JWT过滤器
 * @author qiaoxide
 * 执行流程 preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 */
public class GuideCoreJwtFilter extends BasicHttpAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuideCoreJwtFilter.class);

    /**
     * 当拒绝访问时跳用这个函数 ，返回false
     * 检测header里面是否包含Authorization字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        // TODO Auto-generated method stub

        LOGGER.info("isLoginAttempt");

        return true;
    }

    /**
     * 是否拦截
     */
    @Override
    protected boolean isAccessAllowed(
            ServletRequest request, ServletResponse response, Object mappedValue) {
        // TODO Auto-generated method stub

        LOGGER.info("isAccessAllowed");

        String auth = getAuthzHeader(request);
        if (auth != null && !auth.equals("")) {

            try {
                JwtToken token = new JwtToken(auth, "guidecore");
                // 检查认证
                getSubject(request, response).login(token);

                LOGGER.info("验证通过");
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
        // TODO Auto-generated method stub
        LOGGER.info("onAccessDenied");
        return false;
    }
    //	/**
    //     * 提供跨域支持
    //     */
    //    @Override
    //    protected boolean preHandle(ServletRequest request, ServletResponse response) throws
    // Exception {
    //        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    //        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    //        httpServletResponse.setHeader("Access-Control-Allow-Origin",
    // httpServletRequest.getHeader("Origin"));
    //        httpServletResponse.setHeader("Access-Control-Allow-Methods",
    // "GET,POST,OPTIONS,PUT,DELETE");
    //        httpServletResponse.setHeader("Access-Control-Allow-Headers",
    // httpServletRequest.getHeader("Access-Control-Request-Headers"));
    //        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
    //        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
    //            httpServletResponse.setStatus(HttpStatus.OK.value());
    //            return false;
    //        }
    //        return super.preHandle(request, response);
    //    }

}
