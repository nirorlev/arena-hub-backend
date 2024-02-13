package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcFeedBack;
import com.threeatom.guidecore.mapper.GcFeedBackMapper;
import com.threeatom.guidecore.service.GcFeedBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GcFeedBackServiceImpl extends ServiceImpl<GcFeedBackMapper, GcFeedBack>
        implements GcFeedBackService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);
}
