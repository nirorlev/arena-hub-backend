package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.analytic.VideoSearchResultDto;
import com.threeatom.guidecore.entity.GcVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface VideoMapping {

    @Mapping(target = "title", source = "videoName")
    @Mapping(target = "thumbNailUrl", source = "thumbnailUrl")
    VideoSearchResultDto map(GcVideo video);
}
