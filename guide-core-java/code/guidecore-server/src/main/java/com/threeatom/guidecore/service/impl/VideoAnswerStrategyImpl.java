package com.threeatom.guidecore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.OpenQuestionAnswer;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import org.springframework.stereotype.Service;

@Service
public class VideoAnswerStrategyImpl implements VideoAnswerStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Answer createAnswer(TaskType taskType, String jsonAnswer) throws JsonProcessingException {
        return switch (taskType) {
            case PAIRING -> objectMapper.readValue(jsonAnswer, PairingAnswer.class);
            case MULTIPLE_CHOICE -> objectMapper.readValue(jsonAnswer, MultipleChoiceAnswer.class);
            case SINGLE_CHOICE -> objectMapper.readValue(jsonAnswer, SingleChoiceAnswer.class);
            case OPEN_QUESTION -> objectMapper.readValue(jsonAnswer, OpenQuestionAnswer.class);
            case FILL_IN_THE_BLANK -> objectMapper.readValue(jsonAnswer, FillInTheBlankAnswer.class);
        };
    }
}
