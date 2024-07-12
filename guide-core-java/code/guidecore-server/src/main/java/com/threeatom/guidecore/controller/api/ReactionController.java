package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.ReactionDetailsDto;
import com.threeatom.guidecore.enums.ReactionType;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcUserVideoActionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Reactions API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class ReactionController {

    private final GcUserVideoActionService userVideoActionService;
    private final GcUserService userService;

    @PatchMapping(value = "/videos/{videoId}/reactions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update reactions")
    public ResponseEntity<Map<ReactionType, ReactionDetailsDto>> updateReactions(@PathVariable Integer videoId,
                                                @RequestBody @Valid @NotEmpty Map<String, Boolean> reactions,
                                                HttpServletRequest request) {
        Integer userId = userService.getCurrentUser(request).getId();

        userVideoActionService.updateReactions(videoId, userId, reactions);
        return ResponseEntity.ok().body(userVideoActionService.getReactions(videoId, userId));
    }


    @GetMapping(value = "/videos/{videoId}/reactions")
    @ApiOperation(value = "Get reactions")
    public ResponseEntity<Map<ReactionType, ReactionDetailsDto>> getReactions(@PathVariable Integer videoId,
                                                                              HttpServletRequest request) {
        Integer userId = userService.getCurrentUser(request).getId();

        return ResponseEntity.ok().body(userVideoActionService.getReactions(videoId, userId));
    }
}