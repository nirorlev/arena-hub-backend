package com.threeatom.guidecore.util;

import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.enums.UserRole;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

@UtilityClass
public class AuthorizationUtil {

    private static final String ROLE = "role";
    private static final String UID = "uid";
    private static final String AUTHORIZATION = "Authorization";

    public String getRole(HttpServletRequest request) {
        String token = getRequestAuthHeader(request);
        if (token == null) {
            return null;
        }

        return JwtUtil.getValueByToken(token, ROLE);
    }

    public static Optional<String> getPayloadValueByName(String token, String name) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(name)) {
            return Optional.empty();
        }

        return Optional.ofNullable(JwtUtil.getValueByToken(token, name));
    }

    public Integer getUserUid(HttpServletRequest request) {
        String token = getRequestAuthHeader(request);
        return Integer.valueOf(JwtUtil.getValueByToken(token, UID));
    }

    public static String getRequestAuthHeader(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION);
    }

    public boolean isManager(HttpServletRequest request) {
        return UserRole.MANAGER.getRole().equals(getRole(request));
    }

    public boolean isUser(HttpServletRequest request) {
        return UserRole.USER.getRole().equals(getRole(request));
    }
}
