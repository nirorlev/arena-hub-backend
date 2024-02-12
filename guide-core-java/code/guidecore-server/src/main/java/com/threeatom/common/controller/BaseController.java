//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.controller;

import com.threeatom.common.ApiAssert;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.system.entity.SysBusiness;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.entity.SysUser;
import com.threeatom.system.service.SysBusinessService;
import com.threeatom.system.service.SysSystemService;
import com.threeatom.system.service.SysUserService;
import com.threeatom.utils.SpringUtil;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    @Autowired private SysUserService sysUserService;
    @Autowired private SysBusinessService sysBusinessService;
    @Autowired private SysSystemService systemService;
    @Autowired private SysBusiness currentBusiness;
    @Autowired HttpServletRequest request;

    public BaseController() {}

    public SysUser getSysUser() {
        Integer uid = Integer.parseInt(this.getTokenValue("uid"));
        return this.sysUserService.getSysUserByIdCache(uid);
    }

    public String getTokenValue(String field) {
        String token = this.request.getHeader("Authorization");
        // 验证token
        return JwtUtil.getValueByToken(token, field);
    }

    public void clearAuthorizationCache() {
        AuthorizingRealm currentRealm = this.getRealmByName(this.getRealmName());
        currentRealm.getAuthorizationCache().remove(SecurityUtils.getSubject().getPrincipals());
    }

    public String getRealmName() {
        String realmName =
                (String) SecurityUtils.getSubject().getPrincipals().getRealmNames().iterator().next();
        return realmName;
    }

    public AuthorizingRealm getRealmByName(String realmName) {
        List<Realm> realms = (List) SpringUtil.getBean("realms");
        Iterator var3 = realms.iterator();

        Realm realm;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            realm = (Realm) var3.next();
        } while (!realm.getName().equals(realmName));

        return (AuthorizingRealm) realm;
    }

    // 后期需把sysSystem概念删除
    public SysSystem getSystem() {
        //        Integer sysId = Integer.parseInt(this.getTokenValue("sysId"));
        //        return this.systemService.getSystemById(sysId);
        return systemService.getSystem();
    }

    public SysSystem getSystem(String name) {
        List<SysSystem> list =
                this.systemService.getSystemListByBusinessKeyCache(this.currentBusiness.getKey());
        Iterator var3 = list.iterator();

        SysSystem sysSystem;
        do {
            if (!var3.hasNext()) {
                return null;
            }

            sysSystem = (SysSystem) var3.next();
        } while (!sysSystem.getName().equals(name));

        return sysSystem;
    }

    public SysSystem getSystemOne() {
        List<SysSystem> list =
                this.systemService.getSystemListByBusinessKeyCache(this.currentBusiness.getKey());
        return list != null && list.size() > 0 ? (SysSystem) list.get(0) : null;
    }

    public SysBusiness getBusiness(String key) {
        return this.sysBusinessService.getSysBusinessByKeyCache(key);
    }

    public List<SysSystem> getSystemList(String businessKey) {
        return this.systemService.getSystemListByBusinessKeyCache(businessKey);
    }

    public Integer getIntegerParamsStrict(String name) {
        String value = this.request.getParameter(name);
        ApiAssert.notEmpty(value, "参数不存在");
        return Integer.parseInt(value);
    }

    public String getParamsStrict(String name) {
        String value = this.request.getParameter(name);
        ApiAssert.notEmpty(value, "参数不存在");
        return value;
    }

    public Integer getIntegerParams(String name) {
        String value = this.getParams(name);
        return StringUtils.isEmpty(value) ? null : Integer.parseInt(value);
    }

    public String getParams(String name) {
        String value = this.request.getParameter(name);
        return value;
    }
}
