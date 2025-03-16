
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.mapper.VideoEventMapper;
import com.threeatom.guidecore.service.VideoEventService;
import org.springframework.stereotype.Service;

@Service
public class VideoEventServiceImpl extends ServiceImpl<VideoEventMapper, VideoEvent>
        implements VideoEventService {
}
