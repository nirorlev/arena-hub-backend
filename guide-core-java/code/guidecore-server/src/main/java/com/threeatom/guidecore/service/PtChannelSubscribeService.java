package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import java.util.List;

public interface PtChannelSubscribeService extends IService<PtChannelSubscribe> {
    PtChannelSubscribe selectIfSubscribe(Integer userId, Integer channelId);

    void subscribe(GcUser user, Integer channelId);

    void unsubscribe(GcUser user, Integer channelId);

    void autoSubscribeToContentGroupChannels(List<GcAccess> contentGroups, Integer userId);
}
