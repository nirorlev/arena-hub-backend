package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.TaskDto;
import com.threeatom.guidecore.dto.response.ChoiceDto;
import com.threeatom.guidecore.dto.response.QuestionDto;
import com.threeatom.guidecore.entity.MultipleChoiceProperties;
import com.threeatom.guidecore.entity.PairingProperties;
import com.threeatom.guidecore.entity.SingleChoiceProperties;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.TaskChoice;
import com.threeatom.guidecore.entity.VideoEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(uses = UserMapping.class)
public interface TaskMapping {

    List<com.threeatom.guidecore.dto.response.TaskDto> map(List<VideoEvent> videoEvents);

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "timestamp", source = "videoTime")
    @Mapping(target = "retries", source = "task.retries")
    @Mapping(target = "canSkip", source = "task.allowSkip")
    @Mapping(target = "version", source = "task.version")
    @Mapping(target = "question", source = "task", qualifiedByName = "mapQuestion")
    com.threeatom.guidecore.dto.response.TaskDto map(VideoEvent videoEvent);

    @Mapping(target = "choices", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "answer", ignore = true)
    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "question", source = "taskDto.question.text")
    @Mapping(target = "allowSkip", source = "taskDto.canSkip")
    @Mapping(target = "updatedByUserId", source = "updatedByUserId")
    void update(@MappingTarget Task task, TaskDto taskDto, Integer updatedByUserId);

    @Named("mapQuestion")
    default QuestionDto mapQuestion(Task task) {
        if (task == null) {
            return null;
        }
        QuestionDto questionDto = new QuestionDto();
        questionDto.setType(task.getType());
        questionDto.setText(task.getQuestion());
        questionDto.setChoices(mapChoicesDto(task.getChoices()));

        Boolean randomOrder = null;
        Integer minRequiredPairs = null;
        List<List<Integer>> grouping = null;

        switch (task.getType()) {
            case SINGLE_CHOICE -> {
                SingleChoiceProperties properties = (SingleChoiceProperties) task.getProperties();
                randomOrder = properties.isRandomOrder();
            }
            case MULTIPLE_CHOICE -> {
                MultipleChoiceProperties properties = (MultipleChoiceProperties) task.getProperties();
                randomOrder = properties.isRandomOrder();
            }
            case PAIRING -> {
                PairingProperties properties = (PairingProperties) task.getProperties();
                randomOrder = properties.isRandomOrder();
                minRequiredPairs = properties.getMinRequiredPairs();
                grouping = properties.getGrouping();
            }
        }

        questionDto.setRandomChoiceOrder(randomOrder);
        questionDto.setMinRequiredPairs(minRequiredPairs);
        questionDto.setGrouping(grouping);

        return questionDto;
    }

    private List<ChoiceDto> mapChoicesDto(List<TaskChoice> choices) {
        if (CollectionUtils.isEmpty(choices)) {
            return null;
        }

        return choices.stream()
            .sorted(Comparator.comparing(TaskChoice::getOrder))
            .map(this::createChoiceDto)
            .collect(Collectors.toList());
    }

    private ChoiceDto createChoiceDto(TaskChoice taskChoice) {
        ChoiceDto choiceDto = new ChoiceDto();
        choiceDto.setId(taskChoice.getId());
        choiceDto.setText(taskChoice.getContent());
        return choiceDto;
    }
}
