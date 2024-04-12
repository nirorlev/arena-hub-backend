package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.VideoPlaySegment;

public interface VideoPlaySegmentService extends IService<VideoPlaySegment> {
    void saveVideoPlaySegment(VideoPlayDto videoPlayDto);
}
