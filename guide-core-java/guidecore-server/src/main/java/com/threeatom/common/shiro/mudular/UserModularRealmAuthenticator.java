package com.threeatom.common.shiro.mudular;

import com.threeatom.common.jwt.JwtToken;
import com.threeatom.common.shiro.BearerToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {

    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        this.assertRealmsConfigured();

        Collection<Realm> realms = this.getRealms();
        List<Realm> typeRealms = new ArrayList<>();

        // 保存与登录类型对应的Realm
        for (Realm realm : realms) {
            if (authenticationToken instanceof JwtToken) {
                handleJwtRealm((JwtToken) authenticationToken, realm, typeRealms);
            } else if (authenticationToken instanceof BearerToken) {
                handleBearerRealm((BearerToken) authenticationToken, realm, typeRealms);
            }
        }

        if (typeRealms.size() == 1) {
            return this.doSingleRealmAuthentication((Realm) typeRealms.get(0), authenticationToken);
        }

        return this.doMultiRealmAuthentication(typeRealms, authenticationToken);
    }

    private void handleJwtRealm(JwtToken authenticationToken, Realm realm, List<Realm> typeRealms) {
        if (realm.getName().equals(authenticationToken.getType())) {
            typeRealms.add(realm);
        }
    }

    private void handleBearerRealm(BearerToken authenticationToken, Realm realm, List<Realm> typeRealms) {
        if (realm.getName().equals(authenticationToken.getType())) {
            typeRealms.add(realm);
        }
    }
}
