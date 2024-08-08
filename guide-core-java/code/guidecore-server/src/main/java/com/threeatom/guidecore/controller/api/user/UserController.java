package com.threeatom.guidecore.controller.api.user;

import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import com.threeatom.guidecore.service.UserLicenseService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "User")
@RestController
@RequestMapping(
    value = "/api/v2/users",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserLicenseService userLicenseService;
    private final OrgLicenseLimitService orgLicenseLimitService;
    private final GcUserService userService;

    @ApiOperation(value = "Get publish permissions of the current user according to org license limits")
    @GetMapping("/me/permissions")
    public ResponseEntity<LicensePermissionsDto> getPermissions(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return ResponseEntity.ok().body(orgLicenseLimitService.getPermissions(masterId));
    }

    @ApiOperation(value = "Get usage of the current user according to org license limits")
    @GetMapping("/me/usage")
    public ResponseEntity<LicenseUsageDto> getUsage(HttpServletRequest request) {
        GcUser currentUser = userService.getCurrentUser(request);
        return ResponseEntity.ok().body(userLicenseService.getPermissions(currentUser.getId()));
    }
}
