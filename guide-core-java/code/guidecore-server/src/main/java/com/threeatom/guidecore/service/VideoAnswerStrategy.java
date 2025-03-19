package com.threeatom.guidecore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.enums.TaskType;
import java.util.Map;

public interface VideoAnswerStrategy {

    Answer createAnswer(QuestionDto questionDto, Map<Integer, Integer> taskChoiceTempIdToRealId);

    Answer createAnswer(TaskType type, String answer) throws JsonProcessingException;

    Answer copyAnswer(TaskType type, Answer answer);
}
