package com.threeatom.guidecore.service.impl;

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
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import com.threeatom.guidecore.mapper.VideoPlaySessionMapper;
import com.threeatom.guidecore.mapping.OwnerMapping;
import com.threeatom.guidecore.service.VideoPlaySessionService;
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

@Service
@Transactional
@RequiredArgsConstructor
public class VideoPlaySessionServiceImpl extends ServiceImpl<VideoPlaySessionMapper, VideoPlaySession>
    implements VideoPlaySessionService {

    private final OwnerMapping ownerMapping;

    @Override
    public void saveVideoPlaySession(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId) {
        VideoPlaySession videoPlaySession = new VideoPlaySession();
        videoPlaySession.setId(videoPlayDto.getSessionId());
        videoPlaySession.setUserId(user.getId());
        videoPlaySession.setVideoId(videoId);
        videoPlaySession.setMasterId(masterId);

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
                UserDetailsDto userDetailsDto = ownerMapping.map(user, user.getInfo());
                Map<String, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails =
                    videoViewerDetails(userToViewSessionEntry.getValue());
                return createVideoViewerDto(userDetailsDto, videoIdToVideoViewerDetails);
            })
            .collect(Collectors.toList());
    }

    private Map<String, VideoViewerVideoDetailDto> videoViewerDetails(List<VideoPlaySession> playSessions) {
        Map<Integer, List<VideoPlaySession>> videoIdToViewSessions = playSessions.stream()
            .collect(Collectors.groupingBy(VideoPlaySession::getVideoId));

        return videoIdToViewSessions.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> String.valueOf(entry.getKey()), entry -> calculateVideoViewerDetails(entry.getValue())));
    }

    private VideoViewerVideoDetailDto calculateVideoViewerDetails(List<VideoPlaySession> playSessions) {
        VideoViewerVideoDetailDto videoViewerVideoDetailDto = new VideoViewerVideoDetailDto();
        videoViewerVideoDetailDto.setTotalViewTime(getTotalTimeViewed(getAllViewSegments(playSessions)));
        videoViewerVideoDetailDto.setViewSessions(playSessions.size());
        videoViewerVideoDetailDto.setPercentageViewed(calculatePercentageViewed(playSessions,
            getVideoTime(playSessions.get(0))));
        return videoViewerVideoDetailDto;
    }

    private Integer getVideoTime(VideoPlaySession playSession) {
        return playSession.getVideo().getVideoTime();
    }

    private double calculatePercentageViewed(List<VideoPlaySession> playSessions, int totalVideoTime) {
        int totalTimeViewed = getTotalTimeViewed(mergeSegments(getAllViewSegments(playSessions)));
        return Double.parseDouble(String.format("%.2f", ((double) totalTimeViewed / totalVideoTime) * 100));
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

    private int getTotalTimeViewed(List<VideoPlaySegment> viewSegments) {
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