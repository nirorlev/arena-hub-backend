package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface ContentGroupChannelSubscriptionService extends IService<ContentGroupChannelSubscription> {

    @Deprecated(forRemoval = true)
    List<ContentGroupChannelSubscriptionDto> deprecatedContentGroupSubscriptions(Integer contentGroupId);

    void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user);

    void saveChannelSubscription(List<Integer> contentGroupIds, Integer channelId, Integer userId);

    void savePublicChannels(List<Integer> contentGroupIds, Integer channelId, Integer userId);

    List<Integer> getSubscribedChannelIds(Integer contentGroupId);

    List<Integer> getSubscribedChannelIds(List<Integer> contentGroupIds);

    void removeChannelSubscriptions(List<GcAccess> contentGroups, List<Integer> channelIds);

    Map<String, List<GroupChannelSubscriptionDto>> getContentGroupSubscriptions(Integer contentGroupId);

    Set<Integer> getContentGroupIds(Integer originChannelId);

    void subscribeOrUpdateChannels(PortalUser portalUser, Integer contentGroupId,
                                   SubscribeChannelDto subscribeChannelDto);
}
