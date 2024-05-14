package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.DbAnalyticsResultDto;
import com.threeatom.guidecore.dto.request.AnalyticsFilterDto;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.VideoPlaySession;
import com.threeatom.guidecore.mapper.VideoPlaySessionMapper;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VideoPlaySessionServiceImpl extends ServiceImpl<VideoPlaySessionMapper, VideoPlaySession>
    implements VideoPlaySessionService {

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

}
