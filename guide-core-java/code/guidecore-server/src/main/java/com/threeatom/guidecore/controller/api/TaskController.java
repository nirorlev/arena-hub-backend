package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.TaskDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.VideoEventFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Video task")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v2/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final PortalUserService portalUserService;
    private final GcUserService userService;
    private final VideoEventFacade videoEventFacade;

    @PutMapping("/{taskId}")
    public ResponseEntity<List<com.threeatom.guidecore.dto.response.TaskDto>> updateTask(@PathVariable Integer taskId,
                                                                                         @RequestBody @Valid
                                                                                         TaskDto taskDto,
                                                                                         HttpServletRequest request) {
        List<com.threeatom.guidecore.dto.response.TaskDto> tasks =
            videoEventFacade.updateTask(taskDto, taskId, getPortalUser(request));
        return ResponseEntity.ok(tasks);
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
