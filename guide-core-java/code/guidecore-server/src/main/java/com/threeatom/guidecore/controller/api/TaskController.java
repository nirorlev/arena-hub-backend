package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.TaskDto;
import com.threeatom.guidecore.dto.request.TaskSessionDto;
import com.threeatom.guidecore.dto.request.UserTaskAnswerDto;
import com.threeatom.guidecore.dto.response.AnswerKeyDto;
import com.threeatom.guidecore.dto.response.TaskVersionDto;
import com.threeatom.guidecore.dto.response.UserTaskAnswersDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.TaskSessionService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Video task")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v2/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final PortalUserService portalUserService;
    private final GcUserService userService;
    private final VideoEventFacade videoEventFacade;
    private final TaskSessionService taskSessionService;

    @PutMapping("/{taskId}")
    public ResponseEntity<List<com.threeatom.guidecore.dto.response.TaskDto>> updateTask(@PathVariable Integer taskId,
                                                                                         @RequestBody @Valid
                                                                                         TaskDto taskDto,
                                                                                         HttpServletRequest request) {
        List<com.threeatom.guidecore.dto.response.TaskDto> tasks =
            videoEventFacade.updateTask(taskDto, taskId, getPortalUser(request));
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId, HttpServletRequest request) {
        videoEventFacade.deleteTask(taskId, getPortalUser(request));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/answer-key")
    public ResponseEntity<AnswerKeyDto> answerKey(@PathVariable Integer taskId, HttpServletRequest request) {
        return ResponseEntity.ok(videoEventFacade.taskAnswerKey(taskId, getPortalUser(request)));
    }

    @PostMapping("/{taskId}/sessions")
    public ResponseEntity<Void> createTaskSession(@PathVariable Integer taskId,
                                                  @RequestBody @Valid TaskSessionDto taskSessionDto,
                                                  HttpServletRequest request) {
        taskSessionService.createUpdateTaskSession(taskSessionDto, taskId, getPortalUser(request));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/versions")
    public ResponseEntity<List<TaskVersionDto>> taskVersions(@PathVariable Integer taskId, HttpServletRequest request) {
        return ResponseEntity.ok(videoEventFacade.taskVersions(taskId, getPortalUser(request)));
    }

    @GetMapping("/{taskId}/answers")
    public UserTaskAnswersDto taskAnswers(@PathVariable Integer taskId,
                                          @RequestParam(value = "users", required = false, defaultValue = "me")
                                          String userFilter,
                                          HttpServletRequest request) {
        return videoEventFacade.taskAnswers(taskId, userFilter, getPortalUser(request));
    }

    @PostMapping("/{taskId}/answers")
    public com.threeatom.guidecore.dto.response.UserTaskAnswerDto createTaskAnswer(@PathVariable Integer taskId,
                                                                                   @RequestBody
                                                                                   @Valid
                                                                                   UserTaskAnswerDto userTaskAnswerDto,
                                                                                   HttpServletRequest request) {
        return videoEventFacade.createTaskAnswer(userTaskAnswerDto, taskId, getPortalUser(request));
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
