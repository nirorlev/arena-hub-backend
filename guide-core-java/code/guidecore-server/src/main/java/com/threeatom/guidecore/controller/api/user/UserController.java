package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.threeatom.client.dto.PowtoonAuthDto;
import com.threeatom.common.controller.Message;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.service.UserLicenseService;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.HttpUtil;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.openapi.models.UserRead;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authc.AuthenticationException;
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
    private final PermitService permitService;
    private final GcMasterService gcMasterService;
    private final PtLoginConfigService ptLoginConfigService;
    private final RedisOperator redisOperator;
    private final SysFileService sysFileService;
    private final GcUserAccessService accessService;
    private final SysMenuService sysMenuService;

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

    @ApiOperation(value = "Verify user token, authentication and return user info")
    @GetMapping("/me")
    public Message getUser(HttpServletRequest request) throws PermitContextError, PermitApiError, IOException {
        GcMaster master = getMaster(request);
        Integer masterId = master.getId();
        PtLoginConfig loginConfig = ptLoginConfigService.getByMasterId(masterId);
        loginConfig = ptLoginConfigService.populatePtLoginConfig(loginConfig);

        GcUser user;
        try {
            user = getUserFromToken(RequestUtil.getRequestAuthHeader(request));
            verifyToken(user, loginConfig);
        } catch (AuthenticationException e) {
            return new Message().error(401, e.getMessage());
        }

        updateUserData(request, user);

        UserRead userRoles = permitService.readUser(user.getUsername());
        Integer isGroupAdmin = accessService.getGroupAdmin(user.getId(), masterId);
        boolean isOrgAdmin = isOrgAdmin(userRoles);
        boolean isTeamAdmin = isTeamAdmin(userRoles);

        return new Message().ok()
            .addData("user", user)
            .addData("logoutUrl", loginConfig.getPtRootUrl() + loginConfig.getLogOutUrl())
            .addData("roleMenus", sysMenuService.getSysMenus(userRoles.roles, user, masterId, isGroupAdmin, isOrgAdmin, isTeamAdmin))
            .addData("isGroupAdmin", isGroupAdmin)
            .addData("isOrgAdmin", isOrgAdmin)
            .addData("logoUrl", getLogoUrl(request, master))
            .addData("ptRootUrl", loginConfig.getPtRootUrl());
    }

    private GcMaster getMaster(HttpServletRequest request) {
        Optional<Integer> masterIdOptional = RequestUtil.getMasterId(request);
        if (masterIdOptional.isEmpty()) {
            return gcMasterService.getMaster("Powtoon");
        }

        return gcMasterService.getMasterById(masterIdOptional.get());
    }

    private GcUser getUserFromToken(String tokens) {
        if (!"undefined".equals(tokens)) {
            Integer userId = JwtUtil.getUserIdByToken(tokens);
            GcUser user = userService.getUserByIdCache(userId);
            JwtUtil.verifyToken(tokens, user.getPassword());
            return user;
        }

        throw new AuthenticationException("Token has not found!");
    }

    private void verifyToken(GcUser user, PtLoginConfig ptLoginConfig) {
        if (null == redisOperator.get("PT:" + user.getUsername())
            || null == redisOperator.get("access_token_userid" + user.getId())) {
            throw new AuthenticationException("Login has expired!");
        }

        Map<String, String> body = getTokenRequestBody(user, ptLoginConfig.getClientId());
        String authResponse =
            HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl() + ptLoginConfig.getOauthToken(), body);
        PowtoonAuthDto authInfo = JSONObject.parseObject(authResponse, PowtoonAuthDto.class);
        updateAuthInRedis(user, authInfo);
    }

    private Map<String, String> getTokenRequestBody(GcUser user, String clientId) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", (String) redisOperator.get("PT_refresh_token:" + user.getUsername()));
        return body;
    }

    private boolean isOrgAdmin(UserRead userRoles) {
        if (userRoles == null) {
            return false;
        }

        if (null != userRoles.attributes.get("isOrgAdmin")) {
            return (boolean) userRoles.attributes.get("isOrgAdmin");
        }

        return false;
    }

    private boolean isTeamAdmin(UserRead userRoles) {
        if (userRoles.attributes.get("managedGroups") == null) {
            return false;
        }

        JSONArray jsonArray =
            JSONArray.parseArray(JSON.toJSONString(userRoles.attributes.get("managedGroups")));
        List<String> integers = jsonArray.toJavaList(String.class);
        return !integers.isEmpty();
    }

    private void updateAuthInRedis(GcUser user, PowtoonAuthDto authInfo) {
        redisOperator.set("access_token_userid" + user.getId(), authInfo.getAccessToken(),
            authInfo.getExpiresIn());
        redisOperator.set("PT_refresh_token:" + user.getUsername(), authInfo.getRefreshToken());
    }

    private void updateUserData(HttpServletRequest response, GcUser user) {
        user.setFirstName(user.getInfo().getFirstName());
        user.setLastName(user.getInfo().getLastName());
        sysFileService.getResFullUrl(user.getInfo().getAvatarFile(), response);
    }

    private String getLogoUrl(HttpServletRequest response, GcMaster master) {
        if (Objects.nonNull(master.getLogoId())) {
            SysFile sysFile = sysFileService.getById(master.getLogoId());
            return sysFileService.getResFullUrl(sysFile, response);
        }

        return null;
    }
}
