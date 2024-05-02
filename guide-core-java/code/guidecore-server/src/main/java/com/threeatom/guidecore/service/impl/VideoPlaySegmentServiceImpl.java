package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.VideoPlaySegmentNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (segment.isPresent()) {
            VideoPlaySegment videoPlaySegment = segment.get();
            videoPlaySegmentMapping.mapUpdateSegment(videoPlaySegment, videoPlayDto);
            this.baseMapper.updateSegment(videoPlaySegment);
            return;
        }

        throw new VideoPlaySegmentNotFoundException(
            String.format("Video play segment with id '%s' and session '%s' is not found", videoPlayDto.getSegmentId(),
                videoPlayDto.getSessionId()));
    }
}
