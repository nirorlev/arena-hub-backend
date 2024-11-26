package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.response.ContentDto;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContentMapping {
    @Mapping(target = "videoId", source = "contentId")
    ContentDto map(GcUserSaveContent content);
}
