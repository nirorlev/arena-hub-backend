package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtConfig;
import com.threeatom.guidecore.mapper.PtConfigMapper;
import com.threeatom.guidecore.service.PtConfigService;
import org.springframework.stereotype.Service;

@Service
public class PtConfigServiceImpl extends ServiceImpl<PtConfigMapper, PtConfig>
        implements PtConfigService {}
