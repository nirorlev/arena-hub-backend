package com.threeatom.common.shiro.realm;

import com.threeatom.common.jwt.JwtToken;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.system.entity.SysSystemConfig;
import com.threeatom.system.service.SysSystemService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class CommonApiRealm extends AuthorizingRealm {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonApiRealm.class);
    @Autowired @Lazy private SysSystemService sysService;

    public CommonApiRealm() {}

    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        LOGGER.info("授权");
        return info;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        String tokenStr = (String) token.getCredentials();
        LOGGER.info(tokenStr);

        try {
            Integer sysId = Integer.parseInt(JwtUtil.getValueByToken(tokenStr, "sysid"));
            SysSystemConfig sysConfig = this.sysService.getSystemConfig(sysId);
            if (sysConfig == null) {
                throw new AuthenticationException("TOKEN无效，无法找到相应的用户");
            } else {
                JwtUtil.verifyToken(tokenStr, sysConfig.getWeappSecret());
                return new SimpleAuthenticationInfo(sysConfig, tokenStr, this.getName());
            }
        } catch (Exception var5) {
            throw new AuthenticationException("TOKEN无效！");
        }
    }
}
