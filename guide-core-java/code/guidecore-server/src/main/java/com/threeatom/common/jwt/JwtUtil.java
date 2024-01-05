//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private static long expire_time = 86400L;

    public JwtUtil() {
    }

    public static String createTokenByWeappUser(String weappid, String sessionKey) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(sessionKey);
            String token = JWT.create().withClaim("weappid", weappid).withClaim("jwtid", jwtId).withExpiresAt(new Date(System.currentTimeMillis() + expire_time * 1000L)).sign(algorithm);
            return token;
        } catch (IllegalArgumentException var5) {
            var5.printStackTrace();
            return "";
        }
    }

    public static String createTokenByWeappUser(String weappid, Map<String, String> params, String sessionKey) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(sessionKey);
            Builder builder = JWT.create().withClaim("weappid", weappid).withClaim("jwtid", jwtId);
            if (params != null) {
                params.forEach((key, value) -> {
                    builder.withClaim(key, value);
                });
            }

            String token = builder.withExpiresAt(new Date(System.currentTimeMillis() + expire_time * 1000L)).sign(algorithm);
            return token;
        } catch (IllegalArgumentException var7) {
            var7.printStackTrace();
            return "";
        }
    }

    public static String createTokenByUser(String uid, Map<String, String> params, String password) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(password);
            Builder builder = JWT.create().withClaim("uid", uid).withClaim("jwtid", jwtId);
            if (params != null) {
                params.forEach((key, value) -> {
                    builder.withClaim(key, value);
                });
            }

            String token = builder.withExpiresAt(new Date(System.currentTimeMillis() + expire_time * 1000L)).sign(algorithm);
            return token;
        } catch (IllegalArgumentException var7) {
            var7.printStackTrace();
            return "";
        }
    }

    public static boolean verifyToken(String token, String sessionKey) throws AuthenticationException {
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

    public static String getWeappUserIdByToken(String token) {
        return JWT.decode(token).getClaim("weappid").asString();
    }

    public static String getJwtIdByToken(String token) {
        return JWT.decode(token).getClaim("jwtid").asString();
    }

    //解析jwt
    public static String getValueByToken(String token, String field) {
        return JWT.decode(token).getClaim(field).asString();
    }
}
