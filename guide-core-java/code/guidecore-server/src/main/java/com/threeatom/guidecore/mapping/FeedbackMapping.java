package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.FeedbackDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.enums.FeedbackItemType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = UserMapping.class)
public interface FeedbackMapping {
    @Mapping(target = "content", source = "feedbackDto.text")
    Feedback map(FeedbackDto feedbackDto, FeedbackItemType itemType, Integer itemId, Integer userId);

    @Mapping(target = "text", source = "content")
    com.threeatom.guidecore.dto.response.FeedbackDto map(Feedback feedback);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "content", source = "feedbackDto.text")
    void mapUpdate(@MappingTarget Feedback feedback, FeedbackDto feedbackDto);
}
