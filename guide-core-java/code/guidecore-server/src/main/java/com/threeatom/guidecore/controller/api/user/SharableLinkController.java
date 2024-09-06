package com.threeatom.guidecore.controller.api.user;


import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.SharableListService;
import com.threeatom.guidecore.util.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
public class SharableLinkController {

    private final SharableListService sharableListService;
    private final PortalUserService portalUserService;

    @GetMapping("/videos/{id}/access")
    public ResponseEntity<GroupAccessDto> getGroupsByContentId(@PathVariable("id") Integer id,
                                                               HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(sharableListService.getSharableListByContentId(id, portalUser));
    }

    @GetMapping("/channels/{id}/access")
    public ResponseEntity<GroupAccessDto> getGroupsByChannelId(@PathVariable("id") Integer id,
                                                               HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(sharableListService.getSharableListByChannelId(id, portalUser));
    }

    @GetMapping("/courses/{id}/access")
    public ResponseEntity<GroupAccessDto> getGroupsByCourseId(@PathVariable("id") Integer id,
                                                              HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);

        return ResponseEntity.ok(sharableListService.getSharableListByCourseId(id, portalUser));
    }

    @GetMapping("/playlists/{id}/access")
    public ResponseEntity<GroupAccessDto> getGroupsByPlaylistId(@PathVariable("id") Integer id,
                                                                HttpServletRequest request) {
        Integer userId = JwtUtil.getUserIdByToken(RequestUtil.getRequestAuthHeader(request));

        return ResponseEntity.ok(sharableListService.getSharableListByPlaylistId(id, userId));
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();

        return portalUserService.getByUserAndMasterId(
            JwtUtil.getUserIdByToken(RequestUtil.getRequestAuthHeader(request)), masterId);
    }
}
