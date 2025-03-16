package com.threeatom.guidecore.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeatom.guidecore.dto.request.ChoiceDto;
import com.threeatom.guidecore.dto.request.QuestionDto;
import com.threeatom.guidecore.entity.FillInTheBlankProperties;
import com.threeatom.guidecore.entity.MultipleChoiceProperties;
import com.threeatom.guidecore.entity.OpenQuestionProperties;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.SingleChoiceProperties;
import com.threeatom.guidecore.entity.TaskProperties;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.service.TaskPropertiesStrategy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TaskPropertiesStrategyImpl implements TaskPropertiesStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public TaskProperties createProperties(TaskType type, QuestionDto question,
                                           Map<Integer, Integer> tempChoiceIdToRealChoiceId) {
        return switch (type) {
            case SINGLE_CHOICE -> new SingleChoiceProperties(question.getRandomChoiceOrder());
            case MULTIPLE_CHOICE -> new MultipleChoiceProperties(question.getRandomChoiceOrder());
            case PAIRING -> {
                validatePairingChoices(question);
                yield pairingProperties(question,
                    mapGroupingChoiceIds(question.getGrouping(), tempChoiceIdToRealChoiceId));
            }
            case FILL_IN_THE_BLANK -> new FillInTheBlankProperties();
            case OPEN_QUESTION -> new OpenQuestionProperties();
        };
    }

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

    private void validatePairingChoices(QuestionDto question) {
        Set<Integer> groupedChoiceIds = question.getGrouping().stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
        boolean allChoiceIdsUsedInGrouping = question.getChoices().stream()
            .map(ChoiceDto::getId)
            .allMatch(groupedChoiceIds::contains);

        if (!allChoiceIdsUsedInGrouping) {
            throw new IllegalArgumentException(
                "All task choice ids should be used in grouping field for pairing task type");
        }
    }

    private PairingProperties pairingProperties(QuestionDto question, List<List<Integer>> grouping) {
        PairingProperties pairingProperties = new PairingProperties();
        pairingProperties.setRandomOrder(question.getRandomChoiceOrder());
        pairingProperties.setMinRequiredPairs(question.getMinRequiredPairs());
        pairingProperties.setGrouping(grouping);
        return pairingProperties;
    }

    private List<List<Integer>> mapGroupingChoiceIds(List<List<Integer>> grouping,
                                                     Map<Integer, Integer> tempChoiceIdToRealChoiceId) {
        return grouping.stream()
            .map(groupChoiceIds -> mapGroupChoiceIds(groupChoiceIds, tempChoiceIdToRealChoiceId))
            .collect(Collectors.toList());
    }

    private List<Integer> mapGroupChoiceIds(List<Integer> groupChoiceIds,
                                            Map<Integer, Integer> tempChoiceIdToRealChoiceId) {
        return groupChoiceIds.stream()
            .map(tempChoiceIdToRealChoiceId::get)
            .collect(Collectors.toList());
    }
}
