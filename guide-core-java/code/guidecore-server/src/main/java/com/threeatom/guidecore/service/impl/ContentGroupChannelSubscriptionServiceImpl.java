package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.mapper.ContentGroupChannelSubscriptionMapper;
import com.threeatom.guidecore.mapping.ContentGroupMapping;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.system.service.SysFileService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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

    private final ContentGroupMapping contentGroupMapping;
    private final SysFileService fileService;

    @Override
    public void subscribeChannels(GcAccess contentGroup, List<Integer> channelIds, GcUser user) {
        removeUnsubscribedChannels(contentGroup.getId(), channelIds);

        channelIds.forEach(channelId -> saveChannelSubscription(List.of(contentGroup.getId()), channelId, user.getId()));
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
    @Transactional(readOnly = true)
    public List<Integer> getPublicChannelIds(Integer contentGroupId) {
        return getChannelIds(List.of(contentGroupId), false);
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

    @Override
    public List<ContentGroupChannelSubscriptionDto> getContentGroupSubscriptions(Integer contentGroupId, HttpServletRequest request) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions =
            baseMapper.findByContentGroupId(contentGroupId, true);

        return contentGroupChannelSubscriptions.stream()
            .map(contentGroupChannelSubscription -> updateUrls(contentGroupChannelSubscription, request))
            .map(contentGroupMapping::map)
            .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> getContentGroupIds(Integer originChannelId) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", originChannelId);

        return this.list(queryWrapper).stream()
            .map(ContentGroupChannelSubscription::getContentGroupId)
            .collect(Collectors.toSet());
    }

    private ContentGroupChannelSubscription updateUrls(ContentGroupChannelSubscription contentGroupChannelSubscription, HttpServletRequest request) {
        fileService.updateImageUrls(contentGroupChannelSubscription.getChannel(), request);
        return contentGroupChannelSubscription;
    }

    private List<Integer> getContentGroupIds(List<GcAccess> contentGroups) {
        return contentGroups.stream()
            .map(GcAccess::getId)
            .collect(Collectors.toList());
    }

    private List<Integer> getChannelIds(List<Integer> contentGroupId, boolean subscribed) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.in("content_group_id", contentGroupId);
        queryWrapper.eq("is_subscribed", subscribed);

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
        queryWrapper.eq("is_subscribed", false);

        this.remove(queryWrapper);
    }

    private void saveChannels(List<Integer> contentGroupIds, Integer channelId, boolean isSubscribed, Integer userId) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions = contentGroupIds.stream()
            .map(contentGroupId -> createSubscription(contentGroupId, channelId, isSubscribed, userId))
            .collect(Collectors.toList());

        this.saveBatch(contentGroupChannelSubscriptions);
    }

    private ContentGroupChannelSubscription createSubscription(Integer contentGroupId, Integer channelId,
                                                               boolean isSubscribed, Integer userId) {
        ContentGroupChannelSubscription subscription = new ContentGroupChannelSubscription();

        subscription.setContentGroupId(contentGroupId);
        subscription.setChannelId(channelId);
        subscription.setCreatedByUserId(userId);
        subscription.setIsSubscribed(isSubscribed);

        return subscription;
    }
}
