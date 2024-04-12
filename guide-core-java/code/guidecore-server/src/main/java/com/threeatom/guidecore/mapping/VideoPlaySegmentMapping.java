package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.VideoPlayDto;
import com.threeatom.guidecore.entity.NumberRange;
import com.threeatom.guidecore.entity.VideoPlaySegment;
import com.threeatom.guidecore.entity.VideoPlaySession;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface VideoPlaySegmentMapping {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id.segmentId", source = "segmentId")
    @Mapping(target = "videoPlaySession.id", source = "sessionId")
    @Mapping(target = "segment", qualifiedByName = "createPlaySegment")
    VideoPlaySegment map (VideoPlayDto videoPlayDto, VideoPlaySession videoPlaySession);

    @Named("createPlaySegment")
    default NumberRange<Integer> createPlaySegment(VideoPlayDto videoPlayDto) {
        NumberRange<Integer> playSegment = new NumberRange<>();

        playSegment.setFrom(videoPlayDto.getStartWatchTimeInSeconds());
        playSegment.setTo(videoPlayDto.getEndWatchTimeInSeconds());

        return playSegment;
    }
}
