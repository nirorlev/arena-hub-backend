package com.threeatom.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private static final Long EXPIRE_TIME = 86400L;

    public static String createTokenByUser(String uid, Map<String, String> params, String password) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(password);
            Builder builder = JWT.create()
                .withClaim("uid", uid)
                .withClaim("jwtid", jwtId);
            if (params != null) {
                params.forEach(builder::withClaim);
            }

            return builder
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_TIME * 1000L))
                    .sign(algorithm);
        } catch (IllegalArgumentException var7) {
            var7.printStackTrace();
            return "";
        }
    }

    public static boolean verifyToken(String token, String sessionKey)
            throws AuthenticationException {
        try {
            Algorithm algorithm = Algorithm.HMAC256(sessionKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception var4) {
            LOGGER.error(var4.getClass().getName());
            if (var4 instanceof TokenExpiredException) {
                throw new AuthenticationException("Login has expired!");
            } else {
                throw new AuthenticationException("Validation failed");
            }
        }
    }

    public static Integer getUserIdByToken(String token) {
        return Integer.valueOf(getValueByToken(token, "uid"));
    }

    public static String getValueByToken(String token, String field) {
        return JWT.decode(token).getClaim(field).asString();
    }
}
