package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.ContentGroupChannelSubscriptionMapper;
import com.threeatom.guidecore.mapping.ContentGroupChannelSubscriptionMapping;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.PtChannelService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentGroupChannelSubscriptionServiceImpl
    extends ServiceImpl<ContentGroupChannelSubscriptionMapper, ContentGroupChannelSubscription>
    implements ContentGroupChannelSubscriptionService {

    private final ContentGroupChannelSubscriptionMapping contentGroupChannelSubscriptionMapping;
    @Lazy
    @Autowired
    private PtChannelService channelService;

    @Override
    public List<ContentGroupChannelSubscriptionDto> deprecatedContentGroupSubscriptions(Integer contentGroupId) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions =
            baseMapper.findByContentGroupId(contentGroupId, true);

        return contentGroupChannelSubscriptions.stream()
            .map(contentGroupChannelSubscription -> {
                channelService.updateUrls(contentGroupChannelSubscription.getChannel());
                return contentGroupChannelSubscriptionMapping.mapDeprecated(contentGroupChannelSubscription);
            })
            .collect(Collectors.toList());
    }

    @Override
    public void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user) {
        removeUnsubscribedChannels(contentGroup.getId(), channelIds);

        channelIds.forEach(
            channelId -> saveChannelSubscription(List.of(contentGroup.getId()), channelId, user.getId()));
    }

    @Override
    public void saveChannelSubscription(List<Integer> contentGroupIds, Integer channelId, Integer userId) {
        saveChannels(contentGroupIds, channelId, true, userId);
    }

    @Override
    public void savePublicChannels(List<Integer> contentGroupIds, Integer channelId, Integer userId) {
        saveChannels(contentGroupIds, channelId, false, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSubscribedChannelIds(Integer contentGroupId) {
        return getChannelIds(List.of(contentGroupId), true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSubscribedChannelIds(List<Integer> contentGroupIds) {
        return getChannelIds(contentGroupIds, true);
    }

    @Override
    public void removeChannelSubscriptions(List<GcAccess> contentGroups, List<Integer> channelIds) {
        if (CollectionUtils.isEmpty(contentGroups)) {
            return;
        }

        List<Integer> contentGroupIds = getContentGroupIds(contentGroups);
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("content_group_id", contentGroupIds);
        queryWrapper.in("channel_id", channelIds);

        this.remove(queryWrapper);
    }

    @Override
    public Map<String, List<GroupChannelSubscriptionDto>> getContentGroupSubscriptions(Integer contentGroupId) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions =
            baseMapper.findByContentGroupId(contentGroupId, null);

        return contentGroupChannelSubscriptions.stream()
            .peek(contentGroupChannelSubscription -> channelService.updateUrls(
                contentGroupChannelSubscription.getChannel()))
            .collect(Collectors.groupingBy(ContentGroupChannelSubscription::getChannelId)).entrySet().stream()
            .collect(Collectors.toMap(subscriptionEntry -> String.valueOf(subscriptionEntry.getKey()),
                subscriptionEntry -> contentGroupChannelSubscriptionMapping.map(subscriptionEntry.getValue())));
    }

    @Override
    public Set<Integer> getContentGroupIds(Integer originChannelId) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", originChannelId);

        return this.list(queryWrapper).stream()
            .map(ContentGroupChannelSubscription::getContentGroupId)
            .collect(Collectors.toSet());
    }

    @Override
    public void subscribeOrUpdateChannels(PortalUser portalUser, Integer contentGroupId,
                                          SubscribeChannelDto subscribeChannelDto) {
        ContentGroupChannelSubscription contentGroupChannelSubscription = findByChannelAndContentGroupId(
            subscribeChannelDto.getChannelId(), contentGroupId);

        if (contentGroupChannelSubscription == null) {
            saveChannelSubscription(List.of(contentGroupId), subscribeChannelDto.getChannelId(),
                portalUser.getUserId());
            return;
        }

        contentGroupChannelSubscriptionMapping.updateChannelSubscription(contentGroupChannelSubscription,
            subscribeChannelDto);
        updateById(contentGroupChannelSubscription);
    }

    private ContentGroupChannelSubscription findByChannelAndContentGroupId(Integer channelId,
                                                                           Integer contentGroupId) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("channel_id", channelId);
        queryWrapper.eq("content_group_id", contentGroupId);

        return this.getOne(queryWrapper);
    }

    private List<Integer> getContentGroupIds(List<GcAccess> contentGroups) {
        return contentGroups.stream()
            .map(GcAccess::getId)
            .collect(Collectors.toList());
    }

    private List<Integer> getChannelIds(List<Integer> contentGroupId, boolean subscribed) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("content_group_id", contentGroupId);
        queryWrapper.eq("auto_subscribe", subscribed);

        return getChannelIds(this.list(queryWrapper));
    }

    private List<Integer> getChannelIds(List<ContentGroupChannelSubscription> subscribes) {
        return subscribes.stream()
            .map(ContentGroupChannelSubscription::getChannelId)
            .distinct()
            .collect(Collectors.toList());
    }

    public void removeUnsubscribedChannels(Integer contentGroupId, List<Integer> channelIds) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("content_group_id", contentGroupId);
        queryWrapper.in("channel_id", channelIds);
        queryWrapper.eq("auto_subscribe", false);

        this.remove(queryWrapper);
    }

    private void saveChannels(List<Integer> contentGroupIds, Integer channelId, boolean isSubscribed, Integer userId) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions = contentGroupIds.stream()
            .map(contentGroupId -> createSubscription(contentGroupId, channelId, isSubscribed, userId))
            .collect(Collectors.toList());

        this.saveBatch(contentGroupChannelSubscriptions);
    }

    private ContentGroupChannelSubscription createSubscription(Integer contentGroupId, Integer channelId,
                                                               boolean autoSubscribe, Integer userId) {
        ContentGroupChannelSubscription subscription = new ContentGroupChannelSubscription();

        subscription.setContentGroupId(contentGroupId);
        subscription.setChannelId(channelId);
        subscription.setCreatedByUserId(userId);
        subscription.setAutoSubscribe(autoSubscribe);

        return subscription;
    }
}
