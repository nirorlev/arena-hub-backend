package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.PageableDto;
import com.threeatom.guidecore.dto.response.PlaylistDto;
import com.threeatom.guidecore.dto.response.VideoWithDetailsDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/playlists", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Playlist", value = "Playlist API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class PlaylistController {

    private final GcUserService userService;
    private final GcUserSaveFolderService playlistService;
    private final PortalUserService portalUserService;

    @GetMapping("/owned")
    @ApiOperation(value = "Get a list of playlists owned by the current user")
    public ResponseEntity<List<PlaylistDto>> owned(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(playlistService.ownedPlaylists(portalUser));
    }

    @GetMapping("/subscribed")
    @ApiOperation(value = "Get a list of playlists subscribed by the current user")
    public ResponseEntity<List<PlaylistDto>> subscribed(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(playlistService.subscribed(portalUser));
    }

    @GetMapping("/discoverable")
    @ApiOperation(value = "Get a list of playlists discoverable by the current user")
    public ResponseEntity<PageableDto<PlaylistDto>> discoverable(
        @RequestParam(required = false, defaultValue = "0") Integer pageNum,
        @RequestParam(required = false, defaultValue = "20") Integer pageSize, HttpServletRequest request) {

        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        GcUser currentUser = userService.getCurrentUser(request);
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(playlistService.discoverable(portalUser, pageNum, pageSize));
    }

    @GetMapping("/videos/latest")
    public ResponseEntity<List<VideoWithDetailsDto>> playlistsLatestVideos(HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        PortalUser portalUser = portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);

        return ResponseEntity.ok(playlistService.playlistLatestVideos(portalUser));
    }

}
