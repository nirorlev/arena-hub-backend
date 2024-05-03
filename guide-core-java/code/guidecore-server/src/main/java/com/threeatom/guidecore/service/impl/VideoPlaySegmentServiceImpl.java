package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.VideoPlaySegmentNotFoundException;
import com.threeatom.common.exception.VideoPlaySegmentNotUpdatedException;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySegmentId;
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
            updateVideoPlaySegment(videoPlayDto);
            return;
        }

        videoPlaySessionService.saveVideoPlaySession(videoPlayDto, user, videoId, masterId);
        VideoPlaySession videoPlaySession = videoPlaySessionService.getById(videoPlayDto.getSessionId());
        this.baseMapper.saveSegment(videoPlaySegmentMapping.map(videoPlayDto, videoPlaySession));
    }

    private void updateVideoPlaySegment(VideoPlayDto videoPlayDto) {
        VideoPlaySegmentId videoPlaySegmentId = videoPlaySegmentMapping.map(videoPlayDto);
        Optional<VideoPlaySegment> segment = this.baseMapper.getSegment(videoPlaySegmentId);

        if (segment.isEmpty()) {
            throw new VideoPlaySegmentNotFoundException(
                String.format("Video play segment with id '%s' and session '%s' is not found",
                    videoPlayDto.getSegmentId(),
                    videoPlayDto.getSessionId()));
        }

        VideoPlaySegment videoPlaySegment = segment.get();
        videoPlaySegmentMapping.mapUpdateSegment(videoPlaySegment, videoPlayDto);
        if (this.baseMapper.updateSegment(videoPlaySegment) == 0) {
            log.error("Video play segment with id '{}' and session '{}' was not updated",
                videoPlayDto.getSegmentId(),
                videoPlayDto.getSessionId());

            throw new VideoPlaySegmentNotUpdatedException(
                String.format("Video play segment with id '%s' and session '%s' was not updated",
                    videoPlayDto.getSegmentId(),
                    videoPlayDto.getSessionId()));
        }
    }
}
