//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import java.util.UUID;

public class JwtCreate {
    public static final String userIdLabel = "uid";
    public static final String jwtIdLabel = "jwtid";
    public static final String clientLabel = "client";
    public static final String sysIdLabel = "sysid";
    private static long expire_time = 86400L;

    public JwtCreate() {}

    public static String createToken(String cliStr, String sysId, String uid, String key) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(key);
            Builder builder =
                    JWT.create()
                            .withClaim("uid", uid)
                            .withClaim("jwtid", jwtId)
                            .withClaim("client", cliStr)
                            .withClaim("sysid", sysId);
            String token =
                    builder
                            .withExpiresAt(new Date(System.currentTimeMillis() + expire_time * 1000L))
                            .sign(algorithm);
            return token;
        } catch (IllegalArgumentException var8) {
            var8.printStackTrace();
            return "";
        }
    }

    public static String createTokenCommon(String sysId, String key) {
        try {
            String jwtId = UUID.randomUUID().toString();
            Algorithm algorithm = Algorithm.HMAC256(key);
            Builder builder =
                    JWT.create()
                            .withClaim("jwtid", jwtId)
                            .withClaim("client", "common_api")
                            .withClaim("sysid", sysId);
            String token =
                    builder
                            .withExpiresAt(new Date(System.currentTimeMillis() + expire_time * 1000L))
                            .sign(algorithm);
            return token;
        } catch (IllegalArgumentException var6) {
            var6.printStackTrace();
            return "";
        }
    }
}
