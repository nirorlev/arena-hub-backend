package com.threeatom.guidecore.controller.api;


import com.threeatom.guidecore.dto.response.CommentDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.GcVideoCommentService;
import com.threeatom.guidecore.service.PortalUserService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Comments API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private final GcVideoCommentService commentService;
    private final PortalUserService portalUserService;
    private final GcUserService userService;

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable("videoId") Integer videoId,
                                                           HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(commentService.videoComments(videoId, portalUser));
    }

    @PostMapping("/videos/{videoId}")
    public ResponseEntity<CommentDto> saveComment(@PathVariable("videoId") Integer videoId,
                                                  @RequestBody
                                                  @Valid com.threeatom.guidecore.dto.request.CommentDto commentDto,
                                                  HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(commentService.createVideoComment(videoId, commentDto, portalUser));
    }

    @PutMapping("/{commentId}/videos/{videoId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("videoId") Integer videoId,
                                                    @PathVariable("commentId") Integer commentId,
                                                    @RequestBody
                                                    @Valid com.threeatom.guidecore.dto.request.CommentDto commentDto,
                                                    HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(commentService.updateVideoComment(videoId, commentId, commentDto, portalUser));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable("commentId") Integer commentId,
                                                    HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        commentService.deleteComment(commentId, portalUser);

        return ResponseEntity.ok().build();
    }
}
