package com.threeatom.guidecore.controller.api.user;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.threeatom.client.dto.PowtoonAuthDto;
import com.threeatom.common.controller.Message;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.GcMasterService;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import com.threeatom.guidecore.service.PortalUserService;
import com.threeatom.guidecore.service.PtLoginConfigService;
import com.threeatom.guidecore.service.SysMenuService;
import com.threeatom.guidecore.service.UserLicenseService;
import com.threeatom.guidecore.util.RequestUtil;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;
import com.threeatom.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.HashMap;
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
    private final GcMasterService gcMasterService;
    private final PtLoginConfigService ptLoginConfigService;
    private final RedisOperator redisOperator;
    private final SysFileService sysFileService;
    private final SysMenuService sysMenuService;
    private final PortalUserService portalUserService;

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
    public Message getUser(HttpServletRequest request) {
        GcMaster master = getMaster(request);
        Integer masterId = master.getId();
        PtLoginConfig loginConfig = ptLoginConfigService.getPopulatedPtLoginConfig(masterId);

        GcUser user;
        try {
            user = getUserFromToken(RequestUtil.getRequestAuthHeader(request));
            refreshFromPowtoon(user, loginConfig, masterId);
        } catch (AuthenticationException | IOException | ClientException e) {
            return new Message().error(401, e.getMessage());
        }

        updateUserData(request, user);

        PortalUser portalUser = portalUserService.getByUserAndMasterId(user.getId(), masterId);

        return new Message().ok()
            .addData("user", user)
            .addData("logoutUrl", loginConfig.getPtRootUrl() + loginConfig.getLogOutUrl())
            .addData("roleMenus", sysMenuService.getSysMenus(portalUser))
            .addData("isGroupAdmin", portalUser.isGroupAdmin())
            .addData("isOrgAdmin", portalUser.isOrgAdmin())
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

    private void refreshFromPowtoon(GcUser user, PtLoginConfig ptLoginConfig, Integer masterId)
        throws IOException, ClientException {
        if (null != redisOperator.get("PT:" + user.getUsername())
            && null != redisOperator.get("access_token_userid" + user.getId())) {
            return;
        }

        Map<String, String> body = getTokenRequestBody(user, ptLoginConfig.getClientId());
        String authResponse =
            HttpUtil.sendPostFormUrlencoded(ptLoginConfig.getPtRootUrl() + ptLoginConfig.getOauthToken(), body);
        PowtoonAuthDto authInfo = JSONObject.parseObject(authResponse, PowtoonAuthDto.class);

        updateAuthInRedis(user, authInfo);
        userService.syncPowtoonUser(authInfo.getAccessToken(), ptLoginConfig, masterId);
    }

    private Map<String, String> getTokenRequestBody(GcUser user, String clientId) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", (String) redisOperator.get("PT_refresh_token:" + user.getUsername()));
        return body;
    }

    private void updateAuthInRedis(GcUser user, PowtoonAuthDto authInfo) {
        redisOperator.set("access_token_userid" + user.getId(), authInfo.getAccessToken(),
            authInfo.getExpiresIn());
        redisOperator.set("PT_refresh_token:" + user.getUsername(), authInfo.getRefreshToken());
    }

    private void updateUserData(HttpServletRequest request, GcUser user) {
        user.setFirstName(user.getInfo().getFirstName());
        user.setLastName(user.getInfo().getLastName());
        sysFileService.getResFullUrl(user.getInfo().getAvatarFile(), request);
    }

    private String getLogoUrl(HttpServletRequest request, GcMaster master) {
        if (Objects.nonNull(master.getLogoId())) {
            SysFile sysFile = sysFileService.getById(master.getLogoId());
            return sysFileService.getResFullUrl(sysFile, request);
        }

        return null;
    }
}
