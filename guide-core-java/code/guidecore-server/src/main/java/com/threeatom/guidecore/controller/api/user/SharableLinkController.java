package com.threeatom.guidecore.controller.api.user;


import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.service.GcUserService;
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
    private final GcUserService userService;

    @GetMapping("/videos/{id}/access")
    public ResponseEntity<GroupAccessDto> getGroupsByContentId(@PathVariable("id") Integer id, HttpServletRequest request) {
        Integer userId = userService.getCurrentUser(request).getId();

        return ResponseEntity.ok(sharableListService.getSharableListByContentId(id, userId));
    }
}
