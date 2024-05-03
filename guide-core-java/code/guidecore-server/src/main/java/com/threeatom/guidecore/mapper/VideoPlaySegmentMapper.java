package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySegmentId;
import java.util.Optional;

public interface VideoPlaySegmentMapper extends BaseMapper<VideoPlaySegment> {
    void saveSegment(VideoPlaySegment videoPlaySegment);

    Optional<VideoPlaySegment> getSegment(VideoPlaySegmentId videoPlaySegment);

    Integer updateSegment(VideoPlaySegment videoPlaySegment);
}
