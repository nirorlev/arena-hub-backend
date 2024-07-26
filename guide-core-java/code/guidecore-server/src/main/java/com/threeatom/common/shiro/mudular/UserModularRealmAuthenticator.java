package com.threeatom.common.shiro.mudular;

import com.threeatom.common.jwt.JwtToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {
    public UserModularRealmAuthenticator() {}

    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        // 判断getRealms()是否返回为空
        this.assertRealmsConfigured();
        // 强制转换回自定义的Token
        JwtToken token = (JwtToken) authenticationToken;
        // 获取所有Realm
        Collection<Realm> realms = this.getRealms();
        List<Realm> typeRealms = new ArrayList();

        // 保存与登录类型对应的Realm
        Iterator var5 = realms.iterator();
        while (var5.hasNext()) {
            Realm realm = (Realm) var5.next();
            if (realm.getName().equals(token.getType())) {
                typeRealms.add(realm);
            }
        }
        // 判断是单Realm还是多Realm
        if (typeRealms.size() == 1) {
            return this.doSingleRealmAuthentication((Realm) typeRealms.get(0), token);
        } else {
            return this.doMultiRealmAuthentication(typeRealms, token);
        }
    }
}
