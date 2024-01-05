//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.shiro.mudular;

import com.threeatom.common.shiro.realm.AdminUserRealm;
import java.util.Iterator;
import java.util.Set;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserModularRealmAuthorizer extends ModularRealmAuthorizer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(UserModularRealmAuthorizer.class);
//
//    public UserModularRealmAuthorizer() {
//    }
//
//    public boolean isPermitted(PrincipalCollection principals, String permission) {
//        LOGGER.info("isPermitted---String");
//        this.assertRealmsConfigured();
//        Set<String> realmNames = principals.getRealmNames();
//        String realmName = (String)realmNames.iterator().next();
//        LOGGER.info("realmName:" + realmName);
//        Iterator var5 = this.getRealms().iterator();
//
//        while(var5.hasNext()) {
//            Realm realm = (Realm)var5.next();
//            if (realm instanceof Authorizer) {
//                LOGGER.info("realm:" + realm.getName());
//                if (realm.getName().equals(realmName) && realm instanceof AuthorizingRealm) {
//                    return ((AuthorizingRealm)realm).isPermitted(principals, permission);
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
//        LOGGER.info("isPermitted---Permission");
//        this.assertRealmsConfigured();
//        Set<String> realmNames = principals.getRealmNames();
//        String realmName = (String)realmNames.iterator().next();
//        Iterator var5 = this.getRealms().iterator();
//
//        while(var5.hasNext()) {
//            Realm realm = (Realm)var5.next();
//            if (realm instanceof Authorizer && realm.getName().equals(realmName)) {
//                if (realm instanceof AdminUserRealm) {
//                    return ((AdminUserRealm)realm).isPermitted(principals, permission);
//                }
//
//                if (realm instanceof WeappRealm) {
//                    return ((WeappRealm)realm).isPermitted(principals, permission);
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
//        LOGGER.info("isPermitted---String");
//        this.assertRealmsConfigured();
//        Set<String> realmNames = principals.getRealmNames();
//        String realmName = (String)realmNames.iterator().next();
//        LOGGER.info("realmName:" + realmName);
//        Iterator var5 = this.getRealms().iterator();
//
//        while(var5.hasNext()) {
//            Realm realm = (Realm)var5.next();
//            if (realm instanceof Authorizer) {
//                LOGGER.info("realm:" + realm.getName());
//                if (realm.getName().equals(realmName) && realm instanceof AuthorizingRealm) {
//                    return ((AuthorizingRealm)realm).hasRole(principals, roleIdentifier);
//                }
//            }
//        }
//
//        return false;
//    }
}
