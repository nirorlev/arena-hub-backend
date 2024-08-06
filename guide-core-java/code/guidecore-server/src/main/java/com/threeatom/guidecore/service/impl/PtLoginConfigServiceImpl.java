package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.mapper.PtLoginConfigMapper;
import com.threeatom.guidecore.service.PtLoginConfigService;
import org.springframework.stereotype.Service;

@Service
public class PtLoginConfigServiceImpl extends ServiceImpl<PtLoginConfigMapper, PtLoginConfig>
        implements PtLoginConfigService {}
