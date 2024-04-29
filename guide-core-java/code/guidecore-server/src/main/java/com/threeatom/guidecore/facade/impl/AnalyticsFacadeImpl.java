package com.threeatom.guidecore.facade.impl;

import com.threeatom.guidecore.dto.response.analytic.AnalyticsCountDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.facade.AnalyticsFacade;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class AnalyticsFacadeImpl implements AnalyticsFacade {

    private final PtChannelService channelService;
    private final GcVideoService videoService;
    private final GcAccessService accessService;

    @Override
    public AnalyticsCountDto channelsCount(List<Integer> contentGroupIds, OffsetDateTime start, OffsetDateTime end,
                                           Integer masterId, GcUser currentUser) {
        if (CollectionUtils.isEmpty(contentGroupIds)) {
            return channelsCountForAllContentGroups(start, end, masterId);
        }

        return channelsCountForContentGroups(contentGroupIds, start, end, masterId);
    }

    @Override
    public AnalyticsCountDto videoCount(List<Integer> contentGroupIds, OffsetDateTime start, OffsetDateTime end,
                                        Integer masterId, GcUser currentUser) {
        if (CollectionUtils.isEmpty(contentGroupIds)) {
            return videoCountForAllContentGroups(start, end, masterId);
        }

        return videoCountForContentGroups(contentGroupIds, start, end);
    }

    private AnalyticsCountDto videoCountForContentGroups(List<Integer> contentGroupIds, OffsetDateTime start, OffsetDateTime end) {
        int startTimeVideoCount = videoService.countVideosByContentGroupIds(contentGroupIds, start);
        int endTimeVideoCount = videoService.countVideosByContentGroupIds(contentGroupIds, end);

        return getAnalyticsCountDto(endTimeVideoCount, startTimeVideoCount);
    }

    private AnalyticsCountDto videoCountForAllContentGroups(OffsetDateTime start, OffsetDateTime end,
                                                            Integer masterId) {
        int startTimeVideoCount = videoService.countVideosTillTime(start, masterId);
        int endTimeVideoCount = videoService.countVideosTillTime(end, masterId);

        return getAnalyticsCountDto(endTimeVideoCount, startTimeVideoCount);
    }

    private AnalyticsCountDto channelsCountForContentGroups(List<Integer> contentGroupIds, OffsetDateTime start,
                                                            OffsetDateTime end, Integer masterId) {
        List<GcAccess> contentGroups = accessService.selectMasterIdAndIds(masterId, contentGroupIds);
        if (CollectionUtils.isEmpty(contentGroups)) {
            return getAnalyticsCountDto(0, 0);
        }

        int startTimeChannelsCount = channelService.getChannels(getChannelIds(contentGroups), start).size();
        int endTimeChannelsCount = channelService.getChannels(getChannelIds(contentGroups), end).size();

        return getAnalyticsCountDto(endTimeChannelsCount, startTimeChannelsCount);
    }

    private List<Integer> getChannelIds(List<GcAccess> contentGroupIds) {
        return contentGroupIds.stream()
            .map(GcAccess::getSubscribeJson)
            .filter(Objects::nonNull)
            .flatMap(channels -> channels.toJavaList(Integer.class).stream())
            .collect(Collectors.toList());
    }

    private AnalyticsCountDto channelsCountForAllContentGroups(OffsetDateTime start, OffsetDateTime end, Integer masterId) {
        int startTimeChannelsCount = channelService.getChannels(start, masterId).size();
        int endTimeChannelsCount = channelService.getChannels(end, masterId).size();

        return getAnalyticsCountDto(endTimeChannelsCount, startTimeChannelsCount);
    }

    private AnalyticsCountDto getAnalyticsCountDto(int endTimeChannelsCount, int startTimeChannelsCount) {
        return AnalyticsCountDto.builder()
            .total(endTimeChannelsCount)
            .trendDifference(endTimeChannelsCount - startTimeChannelsCount)
            .build();
    }
}
