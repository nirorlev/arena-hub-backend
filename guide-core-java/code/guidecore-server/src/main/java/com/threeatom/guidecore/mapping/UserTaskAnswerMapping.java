package com.threeatom.guidecore.mapping;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.guidecore.dto.response.TaskAnswerReviewDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDetailDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.entity.UserTaskAnswerReview;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = UserMapping.class)
public interface UserTaskAnswerMapping {

    @Mapping(target = "taskType", source = "task.type")
    UserTaskAnswersDto map(UserTaskAnswer userTaskAnswer);

    @Named("mapUserAnswerDetails")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "info.firstName")
    @Mapping(target = "lastName", source = "info.lastName")
    @Mapping(target = "thumbUrl", source = "info.avatarFile.fileUrl")
    UserTaskAnswerDetailDto mapUserAnswerDetails(GcUser taskAnswerOwner);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "answer", source = ".", qualifiedByName = "mapAnswer")
    @Mapping(target = "taskVersion", source = "taskVersion")
    @Mapping(target = "reviews", source = "userTaskAnswerReviews")
    @Mapping(target = "creationTime", source = "createdTime")
    UserTaskAnswerDto mapUserAnswer(UserTaskAnswer userTaskAnswer);

    List<UserTaskAnswerDto> mapUserAnswers(List<UserTaskAnswer> userTaskAnswers);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "creationTime", source = "createdTime")
    TaskAnswerReviewDto map(UserTaskAnswerReview userTaskAnswerReview);

    @Named("mapAnswer")
    default String mapAnswer(UserTaskAnswer userTaskAnswer) {
        return JSONObject.toJSONString(userTaskAnswer.getContent());
    }
}
