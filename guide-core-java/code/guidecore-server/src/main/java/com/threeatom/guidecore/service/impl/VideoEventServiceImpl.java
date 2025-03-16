package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.mapper.VideoEventMapper;
import com.threeatom.guidecore.service.VideoEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoEventServiceImpl extends ServiceImpl<VideoEventMapper, VideoEvent> implements VideoEventService {

    @Override
    public List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType, PortalUser portalUser) {
        return baseMapper.videoEventsByType(portalUser.getUserId(), portalUser.getMasterId(), videoId, eventType);
    }
}
