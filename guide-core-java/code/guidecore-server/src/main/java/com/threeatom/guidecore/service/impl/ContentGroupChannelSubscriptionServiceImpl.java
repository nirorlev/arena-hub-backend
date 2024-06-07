package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.mapper.ContentGroupChannelSubscriptionMapper;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentGroupChannelSubscriptionServiceImpl
    extends ServiceImpl<ContentGroupChannelSubscriptionMapper, ContentGroupChannelSubscription>
    implements ContentGroupChannelSubscriptionService {

    @Override
    public void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user) {
        removeUnsubscribedChannels(contentGroup.getId(), channelIds);

        channelIds.forEach(channelId -> saveChannelSubscription(List.of(contentGroup.getId()), channelId, user));
    }

    @Override
    public void saveChannelSubscription(List<Integer> contentGroupIds, Integer channelId, GcUser user) {
        saveChannels(contentGroupIds, channelId, user, true);
    }

    @Override
    public void savePublicChannels(List<Integer> contentGroupIds, Integer channelId, GcUser user) {
        saveChannels(contentGroupIds, channelId, user, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSubscribedChannelIds(Integer contentGroupId) {
        return getChannelIds(contentGroupId, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getPublicChannelIds(Integer contentGroupId) {
        return getChannelIds(contentGroupId, false);
    }

    @Override
    public void removeChannelFromContentGroups(List<GcAccess> contentGroups, PtChannel channel) {
        if (CollectionUtils.isEmpty(contentGroups)) {
            return;
        }

        List<Integer> contentGroupIds = getContentGroupIds(contentGroups);
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("content_group_id", contentGroupIds);
        queryWrapper.eq("channel_id", channel.getId());

        this.remove(queryWrapper);
    }

    private List<Integer> getContentGroupIds(List<GcAccess> contentGroups) {
        return contentGroups.stream()
            .map(GcAccess::getId)
            .collect(Collectors.toList());
    }

    private List<Integer> getChannelIds(Integer contentGroupId, boolean subscribed) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("content_group_id", contentGroupId);
        queryWrapper.eq("is_subscribed", subscribed);

        return this.list(queryWrapper).stream()
            .map(ContentGroupChannelSubscription::getChannelId)
            .collect(Collectors.toList());
    }

    public void removeUnsubscribedChannels(Integer contentGroupId, List<Integer> channelIds) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("content_group_id", contentGroupId);
        queryWrapper.in("channel_id", channelIds);
        queryWrapper.eq("is_subscribed", false);

        this.remove(queryWrapper);
    }

    private void saveChannels(List<Integer> contentGroupIds, Integer channelId, GcUser user, boolean isSubscribed) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions = contentGroupIds.stream()
            .map(contentGroupId -> createSubscription(contentGroupId, channelId, user, isSubscribed))
            .collect(Collectors.toList());

        this.saveBatch(contentGroupChannelSubscriptions);
    }

    private ContentGroupChannelSubscription createSubscription(Integer contentGroupId, Integer channelId, GcUser user,
                                                               boolean isSubscribed) {
        ContentGroupChannelSubscription subscription = new ContentGroupChannelSubscription();

        subscription.setContentGroupId(contentGroupId);
        subscription.setChannelId(channelId);
        subscription.setCreatedByUserId(user.getId());
        subscription.setIsSubscribed(isSubscribed);

        return subscription;
    }
}
