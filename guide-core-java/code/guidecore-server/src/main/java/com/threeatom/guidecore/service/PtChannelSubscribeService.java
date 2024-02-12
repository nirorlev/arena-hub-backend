package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PtChannelSubscribe;

/**
 * 系统帮助-反馈
 *
 * @author huangpei
 * @Date 2021-10-26
 */
public interface PtChannelSubscribeService extends IService<PtChannelSubscribe> {
    PtChannelSubscribe selectIfSubscribe(Integer userId, Integer channelId);
}
