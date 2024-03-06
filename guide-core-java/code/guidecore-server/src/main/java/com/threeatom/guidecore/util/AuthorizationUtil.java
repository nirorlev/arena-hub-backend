package com.threeatom.guidecore.util;

import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.enums.UserRole;
import javax.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthorizationUtil {

    private static final String ROLE = "role";
    private static final String UID = "uid";
    private static final String AUTHORIZATION = "Authorization";

    public String getRole(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        return JwtUtil.getValueByToken(token, ROLE);
    }

    public Integer getUserUid(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        return Integer.valueOf(JwtUtil.getValueByToken(token, UID));
    }

    public boolean isManager(HttpServletRequest request) {
        return UserRole.MANAGER.getRole().equals(getRole(request));
    }

    public boolean isUser(HttpServletRequest request) {
        return UserRole.USER.getRole().equals(getRole(request));
    }
}
