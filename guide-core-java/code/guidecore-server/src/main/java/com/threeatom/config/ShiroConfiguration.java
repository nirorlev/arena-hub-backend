package com.threeatom.config;

import com.threeatom.common.redis.ShiroRedisCacheManager;
import com.threeatom.common.shiro.filter.AdminJwtFilter;
import com.threeatom.common.shiro.filter.BearerTokenAuthenticatingFilter;
import com.threeatom.common.shiro.filter.WeappJwtFilter;
import com.threeatom.common.shiro.mudular.UserModularRealmAuthenticator;
import com.threeatom.common.shiro.realm.AdminUserRealm;
import com.threeatom.common.shiro.realm.BearerTokenRealm;
import com.threeatom.guidecore.shiro.GuideCoreJwtFilter;
import com.threeatom.guidecore.shiro.GuideCoreUserRealm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class ShiroConfiguration {

    @Bean("adminUserRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public AdminUserRealm adminUserRealm() {

        AdminUserRealm adminUserRealm = new AdminUserRealm();
        adminUserRealm.setName("admin");
        return adminUserRealm;
    }

    @Bean("guideCoreUserRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public GuideCoreUserRealm guideCoreUserRealm() {

        GuideCoreUserRealm realm = new GuideCoreUserRealm();
        realm.setName("guidecore");
        return realm;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public BearerTokenRealm bearerTokenRealm() {
        BearerTokenRealm bearerTokenRealm = new BearerTokenRealm();
        bearerTokenRealm.setName("adminBearer");
        return bearerTokenRealm;
    }

    private ShiroRedisCacheManager cacheManager(RedisTemplate<String, Object> template) {
        return new ShiroRedisCacheManager(template);
    }

    @Value("${frontendPath}")
    private String hubUrl;

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 拦截器.
        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new HashMap<>();
        // 设置我们自定义的JWT过滤器
        filterMap.put("weappjwt", new WeappJwtFilter());
        filterMap.put("adminjwt", new AdminJwtFilter());
        filterMap.put("guidecorejwt", new GuideCoreJwtFilter());
        filterMap.put("adminBearer", new BearerTokenAuthenticatingFilter());

        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/api/*/weapp/runCheck", "anon");
        filterChainDefinitionMap.put("/api/*/weapp/user/login", "anon");
        filterChainDefinitionMap.put("/api/*/weapp/practice/bindlogin", "anon");
        filterChainDefinitionMap.put("/api/*/admin/user/login", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/sendEmailCaptcha", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/changePassword", "anon");

        filterChainDefinitionMap.put("/api/*/guidecore/callback/**", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/login", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/register", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/nativeapp/login", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/nativeapp/register", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/weapp/login", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/weapp/bindRegister", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/weapp/sendEmailCaptcha", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/weapp/changePassword", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/user/weapp/bindLogin", "anon");

        filterChainDefinitionMap.put("/api/*/guidecore/homeInfo/getForHome", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/homeInfo/*", "anon");
        filterChainDefinitionMap.put("/api/*/guidecore/subversion/gcManagerCollection", "anon");

        filterChainDefinitionMap.put("/api/*/guidecore/subjectIntroInfo/getSubjectIntroInfo/*", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/newui/help/center/getFaq", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/homePage", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/search", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/relatedCourse", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/courseDetail", "anon");
        filterChainDefinitionMap.put("/api/v1/powtoon/home/getToken", "anon");

        // 测试用
        filterChainDefinitionMap.put("/api/*/guidecore/user/test", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/video/search", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/newui/user/videoDetail", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/user/videoAllComment/*", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/user/selectComment", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/user/youtube/getYoutubeUrl", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/file/getUploadCert", "anon");
        filterChainDefinitionMap.put("/api/v1/powtoon/home/portalInfosUnlogin", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/user/youtube/getKalturaVideos", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/portalInfosUnlogin", "anon");
        filterChainDefinitionMap.put("/api/v1/govidigo/home/getSubjectInfoBySubId", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/getTagSubjectList", "anon");
        filterChainDefinitionMap.put("/api/v1/powtoon/home/getPtMessage", "anon");
        filterChainDefinitionMap.put("/api/v1/fred/version", "anon"); // James test
        filterChainDefinitionMap.put("/api/v1/version", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/subject/navigation", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/video/subjectId/*", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/user/updateData", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/sitemap/*", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/html/*", "anon");
        filterChainDefinitionMap.put(
                "/api/v1/guidecore/homeInfo/html/hub", "anon"); // James - trying to get SSR working
        filterChainDefinitionMap.put(
                "/api/v1/guidecore/homeInfo/html/hub/**",
                "anon"); // James - trying to get SSR working, I don't think we need the next line if this
        // works
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/html/hub/*", "anon"); // 新增测试
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/html/" + hubUrl, "anon"); //
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/html/" + hubUrl + "/**", "anon"); //
        filterChainDefinitionMap.put("/api/v1/guidecore/homeInfo/html/" + hubUrl + "/*", "anon"); //
        filterChainDefinitionMap.put("/api/v1/guidecore/screenrock/login", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/screenrock/sendEmailCaptcha", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/screenrock/changeUserInfo", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/screenrock/register", "anon");
        filterChainDefinitionMap.put("/api/v1/guidecore/screenrock/videoDetail", "anon");
        filterChainDefinitionMap.put("/api/v1/powtoon/home/updateData", "anon");

        filterChainDefinitionMap.put("/api/v2/app-config", "anon");
        filterChainDefinitionMap.put("/api/v2/webhook", "anon");

        filterChainDefinitionMap.put("/api/v2/admin/**", "adminBearer");
        filterChainDefinitionMap.put("/api/*/guidecore/**", "guidecorejwt");
        filterChainDefinitionMap.put("/api/v2/**", "guidecorejwt");
        filterChainDefinitionMap.put("/api/*/weapp/**", "weappjwt");
        filterChainDefinitionMap.put("/api/*/admin/**", "adminjwt");
        filterChainDefinitionMap.put("/OKapi/v1/powtoon/home/videoDetailPt", "anon");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("realms")
    public List<Realm> realms(
        AdminUserRealm adminUserRealm, GuideCoreUserRealm guideCoreUserRealm, BearerTokenRealm bearerTokenRealm) {
        List<Realm> realms = new ArrayList<>();
        realms.add(adminUserRealm);
        realms.add(bearerTokenRealm);
        realms.add(guideCoreUserRealm);
        return realms;
    }

    @Bean
    public SessionsSecurityManager securityManager(
            List<Realm> realms, RedisTemplate<String, Object> template) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入缓存管理器
        securityManager.setCacheManager(this.cacheManager(template));
        // 无状态subjectFactory设置
        DefaultSessionStorageEvaluator evaluator =
                (DefaultSessionStorageEvaluator)
                        ((DefaultSubjectDAO) securityManager.getSubjectDAO()).getSessionStorageEvaluator();
        evaluator.setSessionStorageEnabled(Boolean.FALSE);

        securityManager.setAuthenticator(new UserModularRealmAuthenticator());

        securityManager.setRealms(realms);
        return securityManager;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
