package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.FeedbackDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.FeedbackItemType;
import com.threeatom.guidecore.facade.FeedbackFacade;
import com.threeatom.guidecore.service.FeedbackService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Feedback")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeedbackController {

    private final FeedbackFacade feedbackFacade;
    private final GcUserService userService;
    private final PortalUserService portalUserService;

    @PostMapping("/{itemType}/{itemId}")
    public ResponseEntity<FeedbackDto> createFeedback(@PathVariable FeedbackItemType itemType,
                                                      @PathVariable Integer itemId,
                                                      @RequestBody @Valid
                                                      com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                                      HttpServletRequest request) {
        return ResponseEntity.ok(feedbackFacade.createFeedback(itemType, itemId, feedbackDto, getPortalUser(request)));
    }

    @PutMapping("/{feedbackId}")
    public ResponseEntity<FeedbackDto> updateFeedback(@PathVariable Long feedbackId,
                                                      @RequestBody @Valid
                                                      com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                                      HttpServletRequest request) {
        return ResponseEntity.ok(feedbackFacade.updateFeedback(feedbackId, feedbackDto, getPortalUser(request)));
    }

    @PutMapping("/{itemType}/{itemId}")
    public ResponseEntity<FeedbackDto> updateLatestOrCreateFeedback(@PathVariable FeedbackItemType itemType,
                                                                    @PathVariable Integer itemId,
                                                                    @RequestBody @Valid
                                                                    com.threeatom.guidecore.dto.request.FeedbackDto feedbackDto,
                                                                    HttpServletRequest request) {
        return ResponseEntity.ok(
            feedbackFacade.updateLatestOrCreateFeedback(itemType, itemId, feedbackDto, getPortalUser(request)));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId, HttpServletRequest request) {
        feedbackFacade.deleteFeedback(feedbackId, getPortalUser(request));
        return ResponseEntity.noContent().build();
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}