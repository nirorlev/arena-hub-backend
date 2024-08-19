package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.mapper.PtLoginConfigMapper;
import com.threeatom.guidecore.service.PtLoginConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtLoginConfigServiceImpl extends ServiceImpl<PtLoginConfigMapper, PtLoginConfig>
    implements PtLoginConfigService {

    private final Environment env;

    @Override
    @Transactional(readOnly = true)
    public PtLoginConfig getByMasterId(Integer masterId) {
        QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
        loginConfigQueryWrapper.eq("master_id", masterId);
        return getOne(loginConfigQueryWrapper);
    }

    @Override
    public PtLoginConfig populatePtLoginConfig(PtLoginConfig ptLoginConfig) {
        if (ptLoginConfig == null) {
            return createNewLoginConfig();
        }

        updateWithEnvironmentVariables(ptLoginConfig);
        return ptLoginConfig;
    }

    private void updateWithEnvironmentVariables(PtLoginConfig ptLoginConfig) {
        if (null == ptLoginConfig.getOauthToken() || ptLoginConfig.getOauthToken().isEmpty()) {
            ptLoginConfig.setOauthToken(env.getProperty("oauthToken"));
        }
        if (null == ptLoginConfig.getUserUrl() || ptLoginConfig.getUserUrl().isEmpty()) {
            ptLoginConfig.setUserUrl(env.getProperty("userUrl"));
        }
        if (null == ptLoginConfig.getLogOut() || ptLoginConfig.getLogOut().isEmpty()) {
            ptLoginConfig.setLogOut(env.getProperty("logOut"));
        }
        if (null == ptLoginConfig.getLogOutUrl() || ptLoginConfig.getLogOutUrl().isEmpty()) {
            ptLoginConfig.setLogOutUrl(env.getProperty("logoutUrl"));
        }
        if (null == ptLoginConfig.getGroups() || ptLoginConfig.getGroups().isEmpty()) {
            ptLoginConfig.setGroups(env.getProperty("groups"));
        }
    }

    private PtLoginConfig createNewLoginConfig() {
        PtLoginConfig ptLoginConfig;
        ptLoginConfig = new PtLoginConfig();
        ptLoginConfig.setOauthToken(env.getProperty("oauthToken"));
        ptLoginConfig.setUserUrl(env.getProperty("userUrl"));
        ptLoginConfig.setLogOut(env.getProperty("logOut"));
        ptLoginConfig.setLogOutUrl(env.getProperty("logoutUrl"));
        ptLoginConfig.setGroups(env.getProperty("groups"));
        return ptLoginConfig;
    }
}
