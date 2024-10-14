package com.threeatom.guidecore.util;

import java.util.Arrays;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class RequestUtil {

    private static final String MASTER_ID = "masterId";
    private static final String AUTHORIZATION = "Authorization";

    public static String getRequestAuthHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    public static Optional<Integer> getMasterId(HttpServletRequest request) {
        String masterId = request.getHeader(MASTER_ID);
        if (StringUtils.isEmpty(masterId)) {
            return Optional.empty();
        }

        return Optional.of(Integer.valueOf(masterId));
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(cookieName))
            .map(Cookie::getValue)
            .findFirst();
    }

    public static Optional<HttpServletRequest> extractCurrentRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(requestAttributes).flatMap(attributes -> Optional.of(attributes.getRequest()));
    }
}
