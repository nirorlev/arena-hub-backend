//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.jwt;

import org.apache.shiro.authc.AuthenticationToken;

public class JwtToken implements AuthenticationToken {
    private static final long serialVersionUID = 1L;
    public static final String ADMIN = "admin";
    public static final String WEAPP = "weapp";
    private String token;
    private String type;

    public JwtToken(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public Object getPrincipal() {
        return this.token;
    }

    public Object getCredentials() {
        return this.token;
    }

    public String getToken() {
        return this.token;
    }

    public String getType() {
        return this.type;
    }
}
