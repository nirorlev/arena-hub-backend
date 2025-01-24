package com.threeatom.guidecore.controller.filter;

import static com.threeatom.guidecore.util.RequestUtil.getRequestAuthHeader;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.guidecore.util.AuthorizationUtil;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MasterIdVerificationFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(MasterIdVerificationFilter.class);

    private static final String MASTER_ID = "masterId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            String masterIdHeader = httpServletRequest.getHeader(MASTER_ID);

            if (masterIdHeader == null) {
                filterChain.doFilter(request, servletResponse);
                return;
            }

            String token = getRequestAuthHeader(httpServletRequest);
            Optional<String> masterIdJwtOptional = AuthorizationUtil.getPayloadValueByName(token, MASTER_ID);

            if (masterIdJwtOptional.isEmpty()) {
                filterChain.doFilter(request, servletResponse);
                return;
            }

            if (!masterIdHeader.equals(masterIdJwtOptional.get())) {
                LOG.error("The masterId in header '{}' and JWT payload '{}' do not match for request path '{}'", masterIdHeader,
                    masterIdJwtOptional.get(), httpServletRequest.getRequestURI());
                throw new ForbiddenException("The masterId in header and JWT payload do not match");
            }
        } catch (Exception e) {
            LOG.error("Error while verifying masterId", e);
        }

        filterChain.doFilter(request, servletResponse);
    }
}
