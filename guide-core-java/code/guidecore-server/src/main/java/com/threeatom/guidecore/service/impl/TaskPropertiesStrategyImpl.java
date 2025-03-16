package com.threeatom.guidecore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeatom.guidecore.entity.FillInTheBlankProperties;
import com.threeatom.guidecore.entity.MultipleChoiceProperties;
import com.threeatom.guidecore.entity.OpenQuestionProperties;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.SingleChoiceProperties;
import com.threeatom.guidecore.entity.TaskProperties;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import org.springframework.stereotype.Service;

@Service
public class TaskPropertiesStrategyImpl implements TaskPropertiesStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TaskProperties createProperties(TaskType taskType, String jsonProperties) throws JsonProcessingException {
        return switch (taskType) {
            case SINGLE_CHOICE -> objectMapper.readValue(jsonProperties, SingleChoiceProperties.class);
            case MULTIPLE_CHOICE -> objectMapper.readValue(jsonProperties, MultipleChoiceProperties.class);
            case PAIRING -> objectMapper.readValue(jsonProperties, PairingProperties.class);
            case FILL_IN_THE_BLANK -> objectMapper.readValue(jsonProperties, FillInTheBlankProperties.class);
            case OPEN_QUESTION -> objectMapper.readValue(jsonProperties, OpenQuestionProperties.class);
        };
    }
}
