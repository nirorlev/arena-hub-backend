package com.threeatom.guidecore.util;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

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
}
