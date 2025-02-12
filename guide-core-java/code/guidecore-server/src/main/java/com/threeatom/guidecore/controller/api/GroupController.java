package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.facade.GroupFacade;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.util.RequestUtil;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupController {

    private final GcUserService gcUserService;
    private final GroupFacade groupFacade;
    private final PortalUserService portalUserService;

    @GetMapping
    public ResponseEntity<GroupResponseDto> groups(HttpServletRequest request) {
        PortalUser portalUser = getPortalUser(request);
        GroupResponseDto groupResponseDto = groupFacade.groups(portalUser);

        return ResponseEntity.ok(groupResponseDto);
    }

    private PortalUser getPortalUser(HttpServletRequest request) {
        GcUser currentUser = gcUserService.getCurrentUser(request);
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return portalUserService.getByUserAndMasterId(currentUser.getId(), masterId);
    }
}
