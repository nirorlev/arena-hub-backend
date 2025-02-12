package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.AssignChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface ContentGroupChannelSubscriptionService
    extends IService<ContentGroupChannelSubscription> {

    void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user);

    void saveChannelSubscription(List<Integer> contentGroupIds, Integer channelId, Integer userId);

    void savePublicChannels(List<Integer> contentGroupIds, Integer channelId, Integer userId);

    List<Integer> getSubscribedChannelIds(Integer contentGroupId);

    List<Integer> getSubscribedChannelIds(List<Integer> contentGroupIds);

    void removeChannelsFromContentGroups(List<GcAccess> contentGroups, List<Integer> channelIds);

    List<ContentGroupChannelSubscriptionDto> getContentGroupSubscriptions(Integer contentGroupId,
                                                                          HttpServletRequest request);

    Set<Integer> getContentGroupIds(Integer originChannelId);

    void assignChannels(PortalUser portalUser, List<AssignChannelDto> assignChannels);

    void assignChannels(PortalUser portalUser, Integer contentGroupId, List<AssignChannelDto> assignChannels);

    void removeAssignment(Integer channelAssignmentId);

    void updateAssignment(Integer channelAssignmentId, PortalUser portalUser, AssignChannelDto assignChannelDto);
}
