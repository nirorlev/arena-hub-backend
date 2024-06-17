package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import com.threeatom.guidecore.mapper.PtchannelSubscribeMapper;
import com.threeatom.guidecore.service.PtChannelSubscribeService;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PtChannelSubscribeServiceImpl extends ServiceImpl<PtchannelSubscribeMapper, PtChannelSubscribe>
    implements PtChannelSubscribeService {

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
            createChannelSubscribe(user, channelId, true);
            return;
        }

        baseMapper.updateIsDeleted(channelSubscribe.getId(), true);
    }

    @Override
    @Transactional
    public void subscribe(GcUser user, Integer channelId) {
        PtChannelSubscribe unsubscribed = getChannelSubscribe(user, channelId, true);

        if (unsubscribed != null) {
            baseMapper.updateIsDeleted(unsubscribed.getId(), false);
            return;
        }

        createChannelSubscribe(user, channelId, false);
    }

    private PtChannelSubscribe getChannelSubscribe(GcUser user, Integer channelId, boolean isDeleted) {
        QueryWrapper<PtChannelSubscribe> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("channel_id", channelId)
            .eq("user_id", user.getId())
            .eq("is_deleted", isDeleted);

        return getOne(queryWrapper);
    }

    private void createChannelSubscribe(GcUser user, Integer channelId, boolean isDeleted) {
        PtChannelSubscribe ptChannelSubscribe = new PtChannelSubscribe();
        ptChannelSubscribe.setUserId(user.getId());
        ptChannelSubscribe.setChannelId(channelId);
        ptChannelSubscribe.setDeleted(isDeleted);
        ptChannelSubscribe.setCreateTime(new Date());
        ptChannelSubscribe.setUpdateTime(new Date());
        save(ptChannelSubscribe);
    }
}
