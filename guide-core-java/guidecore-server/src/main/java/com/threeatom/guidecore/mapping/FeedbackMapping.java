package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.FeedbackDto;
import com.threeatom.guidecore.dto.response.FeedbackAverageDto;
import com.threeatom.guidecore.entity.Feedback;
import com.threeatom.guidecore.entity.FeedbackAverage;
import com.threeatom.guidecore.enums.FeedbackItemType;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(uses = UserMapping.class)
public interface FeedbackMapping {
    @Mapping(target = "content", source = "feedbackDto.text")
    Feedback map(FeedbackDto feedbackDto, FeedbackItemType itemType, Integer itemId, Integer userId);

    @Mapping(target = "text", source = "content")
    @Mapping(target = "creationDate", source = "createdTime")
    @Mapping(target = "userId", source = ".", qualifiedByName = "mapUserId")
    com.threeatom.guidecore.dto.response.FeedbackDto map(Feedback feedback);

    List<com.threeatom.guidecore.dto.response.FeedbackDto> map(List<Feedback> feedbacks);

    @Mapping(target = "id", source = "feedback.id")
    @Mapping(target = "userId", source = "feedback", qualifiedByName = "mapUserId")
    @Mapping(target = "rating", source = "feedback.rating")
    @Mapping(target = "text", source = "feedback.content")
    @Mapping(target = "creationDate", source = "feedback.createdTime")
    @Mapping(target = "anonymous", source = "feedback.anonymous")
    @Mapping(target = "averageRating", source = "feedbackAverage.averageRating")
    @Mapping(target = "feedbacksCount", source = "feedbackAverage.feedbacksCount")
    FeedbackAverageDto map(Feedback feedback, FeedbackAverage feedbackAverage);

    @Mapping(target = "id", ignore = true)
    FeedbackAverageDto map(FeedbackAverage feedbackAverage);

    @Mapping(target = "content", source = "text")
    @Mapping(target = "updatedTime", expression = "java(java.time.OffsetDateTime.now())")
    void mapUpdate(@MappingTarget Feedback feedback, FeedbackDto feedbackDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "content", source = "text")
    @Mapping(target = "updatedTime", expression = "java(java.time.OffsetDateTime.now())")
    void mapPatch(@MappingTarget Feedback feedback, FeedbackDto feedbackDto);

    @Named("mapUserId")
    default Integer mapUserId(Feedback feedback) {
        if (feedback.getAnonymous()) {
            return null;
        }

        return feedback.getUserId();
    }
}
