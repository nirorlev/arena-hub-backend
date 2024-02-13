package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import com.threeatom.guidecore.mapper.PtchannelSubscribeMapper;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PtChannelSubscribeServiceImpl
        extends ServiceImpl<PtchannelSubscribeMapper, PtChannelSubscribe>
        implements PtChannelSubscribeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);

    public PtChannelSubscribe selectIfSubscribe(Integer userId, Integer channelId) {
        QueryWrapper<PtChannelSubscribe> queryWrapper = new QueryWrapper<PtChannelSubscribe>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("channel_id", channelId);
        return this.getOne(queryWrapper);
    }
}
