package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.entity.Answer;
import com.threeatom.guidecore.entity.FillInTheBlankAnswer;
import com.threeatom.guidecore.entity.MultipleChoiceAnswer;
import com.threeatom.guidecore.entity.OpenQuestionAnswer;
import com.threeatom.guidecore.entity.PairingAnswer;
import com.threeatom.guidecore.entity.SingleChoiceAnswer;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.VideoAnswerStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VideoAnswerStrategyImpl implements VideoAnswerStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Answer createAnswer(QuestionDto questionDto, Map<Integer, Integer> taskChoiceTempIdToRealId) {
        String answer = questionDto.getAnswer();

        return switch (questionDto.getType()) {
            case MULTIPLE_CHOICE -> createMultipleChoiceAnswer(answer, taskChoiceTempIdToRealId);
            case SINGLE_CHOICE -> createSingleChoiceAnswer(answer, taskChoiceTempIdToRealId);
            case OPEN_QUESTION -> new OpenQuestionAnswer();
            case FILL_IN_THE_BLANK -> createFillInTheBlankAnswer(answer, taskChoiceTempIdToRealId);
            case PAIRING -> createPairingAnswer(answer, taskChoiceTempIdToRealId);
        };
    }

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

    @Override
    public Answer copyAnswer(TaskType type, Answer answer) {
        return switch (type) {
            case MULTIPLE_CHOICE -> new MultipleChoiceAnswer(answer);
            case SINGLE_CHOICE -> new SingleChoiceAnswer(answer);
            case OPEN_QUESTION -> new OpenQuestionAnswer();
            case FILL_IN_THE_BLANK -> new FillInTheBlankAnswer(answer);
            case PAIRING -> new PairingAnswer(answer);
        };
    }

    private Answer createPairingAnswer(String answer, Map<Integer, Integer> taskChoiceTempIdToRealId) {
        PairingAnswer pairingAnswer = new PairingAnswer();
        List<List<Integer>> tempCorrectChoicePairs = JSON.parseObject(answer, new TypeReference<>() {
        });
        List<List<Integer>> correctChoicePairs = tempCorrectChoicePairs.stream()
            .map(pair -> pair.stream()
                .map(taskChoiceTempIdToRealId::get)
                .collect(Collectors.toList()))
            .collect(Collectors.toList());

        pairingAnswer.setChoiceIds(correctChoicePairs);

        return pairingAnswer;
    }

    private Answer createFillInTheBlankAnswer(String answer, Map<Integer, Integer> taskChoiceTempIdToRealId) {
        FillInTheBlankAnswer fillInTheBlankAnswer = new FillInTheBlankAnswer();
        Map<String, Integer> fillInLabelToTempChoiceId = JSON.parseObject(answer, new TypeReference<>() {
        });
        Map<String, Integer> fillInLabelToChoiceIdMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : fillInLabelToTempChoiceId.entrySet()) {
            Integer correctChoiceId = Optional.ofNullable(taskChoiceTempIdToRealId.get(entry.getValue()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid choice id for fill in the blank question"));

            fillInLabelToChoiceIdMap.put(entry.getKey(), correctChoiceId);
        }
        fillInTheBlankAnswer.setKeywordToAnswer(fillInLabelToChoiceIdMap);

        return fillInTheBlankAnswer;
    }

    private Answer createSingleChoiceAnswer(String answer, Map<Integer, Integer> taskChoiceTempIdToRealId) {
        SingleChoiceAnswer singleChoiceAnswer = new SingleChoiceAnswer();
        Integer tempCorrectChoiceId = Integer.valueOf(answer);
        Integer correctChoiceId = Optional.ofNullable(taskChoiceTempIdToRealId.get(tempCorrectChoiceId))
            .orElseThrow(() -> new IllegalArgumentException("Invalid choice id for single choice question"));

        singleChoiceAnswer.setChoiceId(correctChoiceId);
        return singleChoiceAnswer;

    }

    private Answer createMultipleChoiceAnswer(String answer, Map<Integer, Integer> taskChoiceTempIdToRealId) {
        MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
        List<Integer> correctChoiceIds = JSON.parseArray(answer, Integer.class).stream()
            .filter(taskChoiceTempIdToRealId::containsKey)
            .map(taskChoiceTempIdToRealId::get)
            .collect(Collectors.toList());

        multipleChoiceAnswer.setChoiceIds(correctChoiceIds);
        return multipleChoiceAnswer;
    }
}
