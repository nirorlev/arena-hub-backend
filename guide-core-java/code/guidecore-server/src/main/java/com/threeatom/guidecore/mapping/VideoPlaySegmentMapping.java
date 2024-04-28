package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySegmentId;
import com.threeatom.guidecore.entity.VideoPlaySession;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface VideoPlaySegmentMapping {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.segmentId", source = "videoPlayDto.segmentId")
    @Mapping(target = "id.sessionId", source = "videoPlaySession.id")
    @Mapping(target = "startWatchTimeInSeconds", source = "videoPlayDto.startWatchTimeInSeconds")
    @Mapping(target = "endWatchTimeInSeconds", source = "videoPlayDto.endWatchTimeInSeconds")
    VideoPlaySegment map(VideoPlayDto videoPlayDto, VideoPlaySession videoPlaySession);

    VideoPlaySegmentId map(VideoPlayDto videoPlayDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "startWatchTimeInSeconds", source = "startWatchTimeInSeconds")
    @Mapping(target = "endWatchTimeInSeconds", source = "endWatchTimeInSeconds")
    @Mapping(target = "updateTime", expression = "java(java.time.OffsetDateTime.now())")
    void mapUpdateSegment(@MappingTarget VideoPlaySegment videoPlaySegment, VideoPlayDto videoPlayDto);
}
