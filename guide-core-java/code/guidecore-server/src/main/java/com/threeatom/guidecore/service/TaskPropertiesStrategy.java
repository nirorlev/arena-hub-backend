package com.threeatom.guidecore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.entity.TaskProperties;
import com.threeatom.guidecore.enums.TaskType;
import java.util.Map;

public interface TaskPropertiesStrategy {

    TaskProperties createProperties(TaskType type, QuestionDto question,
                                    Map<Integer, Integer> tempChoiceIdToRealChoiceId);

    TaskProperties createProperties(TaskType type, String jsonProperties) throws JsonProcessingException;
}
