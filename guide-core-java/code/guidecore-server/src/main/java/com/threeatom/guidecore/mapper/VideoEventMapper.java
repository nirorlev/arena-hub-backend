
package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoEventMapper extends BaseMapper<VideoEvent> {
    List<VideoEvent> videoEventsByType(Integer videoId, VideoEventType eventType, Integer userId, Integer masterId);
}
