package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.IdsDto;
import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.dto.response.ChannelLatestVideosDto;
import com.threeatom.guidecore.dto.response.ChannelWithDetailsDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.dto.response.VideoWithSourceDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.PtChannelContentService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Channel")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/channels", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelController {

    private final GcUserService userService;
    private final PtChannelService channelService;
    private final PortalUserService portalUserService;
    private final PtChannelContentService channelContentService;

    @GetMapping("/owned")
    @ApiOperation(value = "Get a list of channels owned by the current user")
    public List<ChannelWithDetailsDto> getOwned(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return channelService.getOwnedChannels(portalUser, request);
    }

    @GetMapping("/subscribed")
    @ApiOperation(value = "Get a list of channels subscribed by the current user")
    public List<ChannelWithDetailsDto> getSubscribedChannels(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return channelService.getSubscribedChannels(portalUser, request);
    }

    @GetMapping("/discoverable")
    @ApiOperation(value = "Get a list of channels discoverable by the current user")
    public List<ChannelWithDetailsDto> getDiscoverableChannels(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return channelService.getDiscoverableChannels(portalUser, request);
    }

    @PostMapping("/sections/order")
    @ApiOperation(value = "Update ordering of channel sections", httpMethod = "POST")
    public ResponseEntity<Void> updateSectionOrder(@RequestBody @Valid IdsDto sectionIds, HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        channelService.updateSectionOrder(sectionIds, masterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/content/order")
    @ApiOperation(value = "Update ordering of channel/section content", httpMethod = "POST")
    public ResponseEntity<Void> updateContentOrder(
        @PathVariable("channelId") Integer channelId, @RequestBody @Valid IdsDto contentIds,
        HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        channelContentService.updateContentOrder(contentIds, channelId, masterId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{channelId}/videos/latest")
    @ApiOperation(value = "Get a list of videos from the channel sections")
    public ResponseEntity<ChannelLatestVideosDto> sectionVideos(@PathVariable("channelId") Integer channelId,
                                                                HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(channelService.sectionLatestVideos(channelId, portalUser));
    }

    @GetMapping("/subscribed/videos/latest")
    @ApiOperation(value = "Get a list of videos from the channel sections")
    public ResponseEntity<List<VideoWithSourceDetailsDto<ChannelDto>>> subscribedChannelVideos(
        HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(channelService.subscribedLatestVideos(portalUser));
    }

    @GetMapping("/{channelId}/videos/{videoId}/player-page")
    @ApiOperation(value = "Get a list of videos from the channel sections")
    public ResponseEntity<VideoWithSourceDetailsDto<VideoSourceDto>> channelVideoPlayerPage(
        @PathVariable("videoId") Integer videoId
        , @PathVariable("channelId") Integer channelId
        , HttpServletRequest request) {

        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(channelService.channelVideoPlayerPage(videoId, channelId, portalUser));
    }
}