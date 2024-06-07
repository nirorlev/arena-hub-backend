package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannel;
import java.util.List;

public interface ContentGroupChannelSubscriptionService
    extends IService<ContentGroupChannelSubscription> {

    void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user);

    void saveChannelSubscription(List<Integer> contentGroupIds, Integer channelId, GcUser user);

    void savePublicChannels(List<Integer> contentGroupIds, Integer channelId, GcUser user);

    List<Integer> getSubscribedChannelIds(Integer contentGroupId);

    List<Integer> getPublicChannelIds(Integer contentGroupId);

    void removeChannelFromContentGroups(List<GcAccess> contentGroups, PtChannel channel);
}
