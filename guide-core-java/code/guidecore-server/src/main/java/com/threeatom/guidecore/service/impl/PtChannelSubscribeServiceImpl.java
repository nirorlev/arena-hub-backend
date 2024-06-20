package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import com.threeatom.guidecore.mapper.PtchannelSubscribeMapper;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PtChannelSubscribeServiceImpl extends ServiceImpl<PtchannelSubscribeMapper, PtChannelSubscribe>
    implements PtChannelSubscribeService {

    private final ContentGroupChannelSubscriptionService contentGroupChannelSubscriptionService;

    @Override
    @Transactional(readOnly = true)
    public PtChannelSubscribe selectIfSubscribe(Integer userId, Integer channelId) {
        QueryWrapper<PtChannelSubscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("channel_id", channelId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void unsubscribe(GcUser user, Integer channelId) {
        PtChannelSubscribe channelSubscribe = getChannelSubscribe(user, channelId, false);

        if (channelSubscribe == null) {
            save(createChannelSubscribe(user, channelId, true));
            return;
        }

        baseMapper.updateIsDeleted(channelSubscribe.getId(), true);
    }

    @Override
    @Transactional
    public void autoSubscribeToContentGroupChannels(GcUser user, List<GcAccess> accessLists) {
        try {
            List<Integer> contentGroupIds = accessLists.stream()
                .map(GcAccess::getId)
                .collect(Collectors.toList());

            List<Integer> contentGroupSubscribed =
                contentGroupChannelSubscriptionService.getSubscribedChannelIds(contentGroupIds);

            List<Integer> channels = getAllByUser(user).stream()
                .map(PtChannelSubscribe::getChannelId)
                .collect(Collectors.toList());

            saveBatch(getAutoSubscribeChannels(user, contentGroupSubscribed, channels));
        } catch (Exception e) {
            log.error("Channel auto-subscription failed for user " + user.getId(), e);
        }
    }

    @Override
    @Transactional
    public void subscribe(GcUser user, Integer channelId) {
        PtChannelSubscribe unsubscribed = getChannelSubscribe(user, channelId, true);

        if (unsubscribed != null) {
            baseMapper.updateIsDeleted(unsubscribed.getId(), false);
            return;
        }

        save(createChannelSubscribe(user, channelId, false));
    }

    private PtChannelSubscribe getChannelSubscribe(GcUser user, Integer channelId, boolean isDeleted) {
        QueryWrapper<PtChannelSubscribe> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("channel_id", channelId)
            .eq("user_id", user.getId())
            .eq("is_deleted", isDeleted);

        return getOne(queryWrapper);
    }

    private List<PtChannelSubscribe> getAllByUser(GcUser user) {
        QueryWrapper<PtChannelSubscribe> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_id", user.getId());

        return list(queryWrapper);
    }

    private List<PtChannelSubscribe> getAutoSubscribeChannels(GcUser user, List<Integer> contentGroupSubscribed,
                                                              List<Integer> allChannels) {
        return contentGroupSubscribed.stream()
            .filter(channelId -> !allChannels.contains(channelId))
            .map(channelId -> createChannelSubscribe(user, channelId, false))
            .collect(Collectors.toList());
    }

    private PtChannelSubscribe createChannelSubscribe(GcUser user, Integer channelId, boolean isDeleted) {
        PtChannelSubscribe channelSubscribe = new PtChannelSubscribe();
        channelSubscribe.setUserId(user.getId());
        channelSubscribe.setChannelId(channelId);
        channelSubscribe.setDeleted(isDeleted);
        channelSubscribe.setCreateTime(new Date());
        channelSubscribe.setUpdateTime(new Date());
        return channelSubscribe;
    }
}
