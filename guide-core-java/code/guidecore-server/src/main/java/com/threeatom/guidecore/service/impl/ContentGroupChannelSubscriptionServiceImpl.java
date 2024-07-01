package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.mapper.ContentGroupChannelSubscriptionMapper;
import com.threeatom.guidecore.mapping.ContentGroupChannelSubscriptionMapping;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.system.service.SysFileService;
import java.util.List;
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

    private final ContentGroupChannelSubscriptionMapping contentGroupChannelSubscriptionMapping;
    private final SysFileService fileService;

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
    public List<Integer> getSubscribedChannelIdsExceptOwned(List<Integer> contentGroupIds, Integer ownerId) {
        return getChannelIds(baseMapper.getSubscribedChannelIdsExceptOwned(contentGroupIds, ownerId));
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

    @Override
    public List<ContentGroupChannelSubscriptionDto> getContentGroupSubscriptions(Integer contentGroupId, HttpServletRequest request) {
        List<ContentGroupChannelSubscription> contentGroupChannelSubscriptions =
            baseMapper.findByContentGroupId(contentGroupId, true);

        return contentGroupChannelSubscriptions.stream()
            .map(contentGroupChannelSubscription -> updateUrls(contentGroupChannelSubscription, request))
            .map(contentGroupChannelSubscriptionMapping::map)
            .collect(Collectors.toList());
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

    private List<Integer> getChannelIds(Integer contentGroupId, boolean subscribed) {
        QueryWrapper<ContentGroupChannelSubscription> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("content_group_id", contentGroupId);
        queryWrapper.eq("is_subscribed", subscribed);

        return getChannelIds(this.list(queryWrapper));
    }

    private List<Integer> getChannelIds(List<ContentGroupChannelSubscription> subscribes) {
        return subscribes.stream()
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
