//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.shiro.realm;

import com.threeatom.common.jwt.JwtToken;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.system.entity.SysPermission;
import com.threeatom.system.entity.SysRole;
import com.threeatom.system.entity.SysUser;
import com.threeatom.system.service.SysPermissionService;
import com.threeatom.system.service.SysRoleService;
import com.threeatom.system.service.SysUserService;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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

public class AdminUserRealm extends AuthorizingRealm {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserRealm.class);
    @Autowired
    @Lazy
    private SysUserService userService;
    @Autowired
    @Lazy
    private SysRoleService roleService;
    @Autowired
    @Lazy
    private SysPermissionService permService;

    public AdminUserRealm() {
    }

    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        LOGGER.info("admin授权");
        Integer userId = (Integer)principals.getPrimaryPrincipal();
        SysUser user = this.userService.getSysUserByIdCache(userId);
        List<SysRole> roles = this.roleService.getUserRoles(user.getId());
        Iterator var6 = roles.iterator();

        while(var6.hasNext()) {
            SysRole role = (SysRole)var6.next();
            if (role.getSuperAdmin().equals(1)) {
                info.addStringPermission("*");
            }
        }

        List<SysPermission> permissions = this.permService.getPermissionByUid(user.getId());
        info.setRoles((Set)roles.parallelStream().map(SysRole::getRoleKey).collect(Collectors.toSet()));
        info.addStringPermissions((Collection)permissions.parallelStream().map(SysPermission::getPermCode).collect(Collectors.toSet()));
        return info;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String tokenStr = (String)token.getCredentials();
        LOGGER.info("admin登录认证");
        LOGGER.info(tokenStr);

        try {
            Integer id = Integer.parseInt(JwtUtil.getValueByToken(tokenStr, "uid"));
            SysUser user = this.userService.getSysUserByIdCache(id);
            if (user == null) {
                throw new AuthenticationException("没有找到账户");
            } else {
                JwtUtil.verifyToken(tokenStr, user.getPassword());
                return new SimpleAuthenticationInfo(user.getId(), tokenStr, this.getName());
            }
        } catch (Exception var5) {
            throw new AuthenticationException("TOKEN无效");
        }
    }
}
