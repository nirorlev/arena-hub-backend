package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.mapper.PtLoginConfigMapper;
import com.threeatom.guidecore.service.PtLoginConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PtLoginConfigServiceImpl extends ServiceImpl<PtLoginConfigMapper, PtLoginConfig>
    implements PtLoginConfigService {

    @Override
    @Transactional(readOnly = true)
    public PtLoginConfig getByMasterId(Integer masterId) {
        QueryWrapper<PtLoginConfig> loginConfigQueryWrapper = new QueryWrapper<>();
        loginConfigQueryWrapper.eq("master_id", masterId);
        return getOne(loginConfigQueryWrapper);
    }
}
