package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.FeedbackItemType;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbacksDto {
    private Map<Integer, UserDetailsDto> users;
    private EnumMap<FeedbackItemType, Map<Integer, FeedbackSummaryDto>> feedbackTypes;
}
