package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.dto.response.CourseVideoBookmarkDto;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VideoPlaySegmentMapping {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.segmentId", source = "videoPlayDto.segmentId")
    @Mapping(target = "id.sessionId", source = "videoPlaySession.id")
    @Mapping(target = "startWatchTimeInSeconds", source = "videoPlayDto.startTime")
    @Mapping(target = "endWatchTimeInSeconds", source = "videoPlayDto.endTime")
    VideoPlaySegment map(VideoPlayDto videoPlayDto, VideoPlaySession videoPlaySession);

    @Mapping(target = "type", expression = "java(com.threeatom.guidecore.enums.CourseContentType.VIDEO)")
    @Mapping(target = "offset", source = "endWatchTimeInSeconds")
    @Mapping(target = "bookmarkDate", source = "updateTime")
    @Mapping(target = "contentId", source = "session.videoId")
    CourseVideoBookmarkDto map(VideoPlaySegment videoPlaySegment);
}
