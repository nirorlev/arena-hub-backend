
package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.UserTaskAnswerDetailDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserTaskAnswer;
import com.threeatom.guidecore.enums.TaskType;
import com.threeatom.guidecore.mapper.UserTaskAnswerMapper;
import com.threeatom.guidecore.mapping.UserTaskAnswerMapping;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTaskAnswerServiceImpl extends ServiceImpl<UserTaskAnswerMapper, UserTaskAnswer>
    implements UserTaskAnswerService {

    private final UserTaskAnswerMapping userTaskAnswerMapping;

    @Override
    public UserTaskAnswersDto findByTaskIdAndUserId(Integer taskId, TaskType taskType, PortalUser portalUser) {
        List<UserTaskAnswer> taskAnswers = baseMapper.findTaskAnswers(taskId, taskType.name(), portalUser.getUserId());

        return convertUserTaskAnswersDtos(taskType, taskAnswers);
    }

    @Override
    public UserTaskAnswersDto findByTaskId(Integer taskId, TaskType taskType) {
        List<UserTaskAnswer> taskAnswers = baseMapper.findTaskAnswers(taskId, taskType.name(), null);

        return convertUserTaskAnswersDtos(taskType, taskAnswers);
    }

    private UserTaskAnswersDto convertUserTaskAnswersDtos(TaskType taskType, List<UserTaskAnswer> userTaskAnswers) {
        UserTaskAnswersDto userTaskAnswersDto = new UserTaskAnswersDto();
        userTaskAnswersDto.setTaskType(taskType);
        userTaskAnswersDto.setUsers(userIdToUserTaskAnswerDetailDto(userIdToUserTaskAnswers(userTaskAnswers)));

        return userTaskAnswersDto;
    }

    private Map<Integer, List<UserTaskAnswer>> userIdToUserTaskAnswers(List<UserTaskAnswer> taskAnswers) {
        return taskAnswers.stream()
            .collect(Collectors.groupingBy(UserTaskAnswer::getOwnerId));
    }

    private Map<Integer, UserTaskAnswerDetailDto> userIdToUserTaskAnswerDetailDto(
        Map<Integer, List<UserTaskAnswer>> userIdToUserTaskAnswers) {
        return userIdToUserTaskAnswers.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                userIdToUserTaskAnswer -> convertToTaskAnswerDetailDto(userIdToUserTaskAnswer.getValue())));
    }

    private UserTaskAnswerDetailDto convertToTaskAnswerDetailDto(List<UserTaskAnswer> userTaskAnswers) {
        GcUser taskAnswersOwner = userTaskAnswers.get(0).getOwner();

        UserTaskAnswerDetailDto userTaskAnswerDetailDto = userTaskAnswerMapping.mapUserAnswerDetails(taskAnswersOwner);
        userTaskAnswerDetailDto.setAnswers(userTaskAnswerMapping.mapUserAnswers(userTaskAnswers));
        return userTaskAnswerDetailDto;
    }
}
