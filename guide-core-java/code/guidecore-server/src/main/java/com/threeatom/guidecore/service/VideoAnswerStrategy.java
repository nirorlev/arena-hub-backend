package com.threeatom.guidecore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.enums.TaskType;

public interface VideoAnswerStrategy {

    Answer createAnswer(TaskType type, String answer) throws JsonProcessingException;
}
