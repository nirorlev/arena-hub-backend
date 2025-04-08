package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.DbAnalyticsResultVideoIdDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.request.VideoViewerDetailsDto;
import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewerDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewerVideoDetailDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import com.threeatom.guidecore.mapper.VideoPlaySessionMapper;
import com.threeatom.guidecore.mapping.UserMapping;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoPlaySessionServiceImpl extends ServiceImpl<VideoPlaySessionMapper, VideoPlaySession>
    implements VideoPlaySessionService {

    private final UserMapping userMapping;

    @Override
    public void saveVideoPlaySession(VideoPlayDto videoPlayDto, Integer videoId, PortalUser portalUser) {
        VideoPlaySession videoPlaySession = new VideoPlaySession();
        videoPlaySession.setId(videoPlayDto.getSessionId());
        videoPlaySession.setUserId(portalUser.getUserId());
        videoPlaySession.setVideoId(videoId);
        videoPlaySession.setMasterId(portalUser.getMasterId());
        videoPlaySession.setClientTime(videoPlayDto.getClientTime());

        save(videoPlaySession);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VideoPlaySession> getVideoPlaySession(UUID sessionId) {
        return Optional.ofNullable(getById(sessionId));
    }

    @Override
    public List<DbAnalyticsResultDto> getVideoViewCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        return baseMapper.getVideoViewCountAnalytics(filter, masterId);
    }

    @Override
    public List<DbAnalyticsResultVideoIdDto> getVideoViewCountByVideoAnalytics(AnalyticsFilterDto filter,
                                                                               Integer masterId) {
        return baseMapper.getVideoViewCountByVideoAnalytics(filter, masterId);
    }

    @Override
    public List<DbAnalyticsResultDto> getViewersCountAnalytics(AnalyticsFilterDto filter, Integer masterId) {
        return baseMapper.getViewersCountAnalytics(filter, masterId);
    }

    @Override
    public List<DbAnalyticsResultVideoIdDto> getViewersCountByVideoAnalytics(AnalyticsFilterDto filter,
                                                                             Integer masterId) {
        return baseMapper.getViewersCountByVideoAnalytics(filter, masterId);
    }

    @Override
    public List<VideoViewerDto> getVideoViewersAnalytics(VideoViewerDetailsDto filter, Integer masterId) {
        List<VideoPlaySession> playSessions = baseMapper.getVideoViewSessionsByViewerDetails(filter, masterId);
        Map<GcUser, List<VideoPlaySession>> userToViewSessions = playSessions.stream()
            .collect(Collectors.groupingBy(VideoPlaySession::getUser));

        return userToViewSessions.entrySet().stream()
            .map(userToViewSessionEntry -> {
                GcUser user = userToViewSessionEntry.getKey();
                UserDetailsDto userDetailsDto = userMapping.map(user);
                Map<String, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails =
                    videoViewerDetails(userToViewSessionEntry.getValue());
                return createVideoViewerDto(userDetailsDto, videoIdToVideoViewerDetails);
            })
            .collect(Collectors.toList());
    }

    @Override
    public Integer getVideoViewsCount(Integer videoId, Integer masterId) {
        QueryWrapper<VideoPlaySession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("video_id", videoId);
        queryWrapper.eq("master_id", masterId);
        return count(queryWrapper);
    }

    private Map<String, VideoViewerVideoDetailDto> videoViewerDetails(List<VideoPlaySession> playSessions) {
        Map<Integer, List<VideoPlaySession>> videoIdToViewSessions = playSessions.stream()
            .collect(Collectors.groupingBy(VideoPlaySession::getVideoId));

        return videoIdToViewSessions.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> String.valueOf(entry.getKey()), entry -> calculateVideoViewerDetails(entry.getValue())));
    }

    @Override
    public Map<Integer, VideoViewerVideoDetailDto> videoViewerDetails(
        List<Integer> videoIds, PortalUser portalUser, OffsetDateTime start, OffsetDateTime end) {

        if (CollectionUtils.isEmpty(videoIds)) {
            return Map.of();
        }

        List<VideoPlaySession> playSessions =
            baseMapper.findByVideoIdsAndUser(videoIds, portalUser.getUserId(), portalUser.getMasterId(), start, end);

        Map<Integer, List<VideoPlaySession>> videoIdToViewSessions = playSessions.stream()
            .collect(Collectors.groupingBy(VideoPlaySession::getVideoId));

        return videoIdToViewSessions.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, entry -> calculateVideoViewerDetails(entry.getValue())));
    }

    private VideoViewerVideoDetailDto calculateVideoViewerDetails(List<VideoPlaySession> playSessions) {
        List<VideoPlaySegment> allViewSegments = getAllViewSegments(playSessions);
        int videoViewedTime = getTimeViewed(mergeSegments(allViewSegments));
        int totalVideoViewedTime = getTimeViewed(allViewSegments);
        VideoViewerVideoDetailDto videoViewerVideoDetailDto = new VideoViewerVideoDetailDto();

        videoViewerVideoDetailDto.setTotalViewTime(totalVideoViewedTime);
        videoViewerVideoDetailDto.setUniqueViewTime(videoViewedTime);
        videoViewerVideoDetailDto.setViewSessions(playSessions.size());
        videoViewerVideoDetailDto.setPercentageViewed(calculatePercentageViewed(
            getVideoTime(playSessions.get(0)), videoViewedTime));
        return videoViewerVideoDetailDto;
    }

    private Integer getVideoTime(VideoPlaySession playSession) {
        return playSession.getVideo().getVideoTime();
    }

    private double calculatePercentageViewed(int totalVideoTime, int videoTimeViewed) {
        return Double.parseDouble(String.format("%.2f", ((double) videoTimeViewed / totalVideoTime) * 100));
    }

    private List<VideoPlaySegment> getAllViewSegments(List<VideoPlaySession> playSessions) {
        return playSessions.stream()
            .flatMap(playSession -> playSession.getViewSegments().stream())
            .collect(Collectors.toList());
    }

    private List<VideoPlaySegment> mergeSegments(List<VideoPlaySegment> segments) {
        List<VideoPlaySegment> sortedSegments = getSortedSegments(segments);
        List<VideoPlaySegment> merged = new ArrayList<>();
        VideoPlaySegment current = sortedSegments.get(0);

        for (int i = 1; i < sortedSegments.size(); i++) {
            VideoPlaySegment segment = sortedSegments.get(i);
            if (current.getEndWatchTimeInSeconds() >= segment.getStartWatchTimeInSeconds()) {
                current.setEndWatchTimeInSeconds(
                    Math.max(current.getEndWatchTimeInSeconds(), segment.getEndWatchTimeInSeconds()));
            } else {
                merged.add(current);
                current = segment;
            }
        }

        merged.add(current);
        return merged;
    }

    private List<VideoPlaySegment> getSortedSegments(List<VideoPlaySegment> segments) {
        return segments.stream()
            .sorted(Comparator.comparingInt(VideoPlaySegment::getStartWatchTimeInSeconds))
            .collect(Collectors.toList());
    }

    private int getTimeViewed(List<VideoPlaySegment> viewSegments) {
        return viewSegments.stream()
            .mapToInt(this::getTimeViewed)
            .sum();
    }

    private int getTimeViewed(VideoPlaySegment viewSegment) {
        return viewSegment.getEndWatchTimeInSeconds() - viewSegment.getStartWatchTimeInSeconds();
    }

    private VideoViewerDto createVideoViewerDto(UserDetailsDto userDetailsDto,
                                                Map<String, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails) {
        VideoViewerDto videoViewerDto = new VideoViewerDto();
        videoViewerDto.setUser(userDetailsDto);
        videoViewerDto.setVideos(videoIdToVideoViewerDetails);
        return videoViewerDto;
    }
}