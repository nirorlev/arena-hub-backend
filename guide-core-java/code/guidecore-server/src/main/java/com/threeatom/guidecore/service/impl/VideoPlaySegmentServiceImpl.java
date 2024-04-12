package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import com.threeatom.guidecore.mapper.VideoPlaySegmentMapper;
import com.threeatom.guidecore.mapping.VideoPlaySegmentMapping;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoPlaySegmentServiceImpl extends ServiceImpl<VideoPlaySegmentMapper, VideoPlaySegment>
    implements VideoPlaySegmentService {

    private final VideoPlaySegmentMapping videoPlaySegmentMapping;
    private final VideoPlaySessionService videoPlaySessionService;

    @Override
    public void saveVideoPlaySegment(VideoPlayDto videoPlayDto) {
        VideoPlaySession videoPlaySession = videoPlaySessionService.getById(videoPlayDto.getSessionId());
        VideoPlaySegment videoPlaySegment = videoPlaySegmentMapping.map(videoPlayDto, videoPlaySession);
        save(videoPlaySegment);
    }
}
