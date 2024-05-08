package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.VideoPlaySegmentNotUpdatedException;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import com.threeatom.guidecore.mapper.VideoPlaySegmentMapper;
import com.threeatom.guidecore.mapping.VideoPlaySegmentMapping;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoPlaySegmentServiceImpl extends ServiceImpl<VideoPlaySegmentMapper, VideoPlaySegment>
    implements VideoPlaySegmentService {

    private final VideoPlaySegmentMapping videoPlaySegmentMapping;
    private final VideoPlaySessionService videoPlaySessionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVideoPlaySegment(VideoPlayDto videoPlayDto, GcUser user, Integer videoId, Integer masterId) {
        Optional<VideoPlaySession> videoPlaySessionOptional =
            videoPlaySessionService.getVideoPlaySession(videoPlayDto.getSessionId());

        if (videoPlaySessionOptional.isPresent()) {
            updateVideoPlaySegment(videoPlayDto, videoPlaySessionOptional.get());
            return;
        }

        videoPlaySessionService.saveVideoPlaySession(videoPlayDto, user, videoId, masterId);
        VideoPlaySession videoPlaySession = videoPlaySessionService.getById(videoPlayDto.getSessionId());
        this.baseMapper.saveOrUpdateSegment(videoPlaySegmentMapping.map(videoPlayDto, videoPlaySession));
    }

    private void updateVideoPlaySegment(VideoPlayDto videoPlayDto, VideoPlaySession videoPlaySession) {
        if (this.baseMapper.saveOrUpdateSegment(videoPlaySegmentMapping.map(videoPlayDto, videoPlaySession)) == 0) {
            log.error(
                "Video play segment id '{}', session '{}' and play segments start '{}' and  end - '{}' was not updated",
                videoPlayDto.getSegmentId(),
                videoPlayDto.getSessionId(),
                videoPlayDto.getStartWatchTimeInSeconds(),
                videoPlayDto.getEndWatchTimeInSeconds());

            throw new VideoPlaySegmentNotUpdatedException(
                String.format("Video play segment with id '%s' and session '%s' was not updated",
                    videoPlayDto.getSegmentId(),
                    videoPlayDto.getSessionId()));
        }
    }
}
