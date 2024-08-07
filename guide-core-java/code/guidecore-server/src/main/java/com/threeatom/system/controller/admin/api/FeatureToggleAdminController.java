package com.threeatom.system.controller.admin.api;

import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.service.FeatureToggleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/api/v2/admin/feature-toggles",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Feature Toggle", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeatureToggleAdminController {

    private final FeatureToggleService featureToggleService;

    @PutMapping
    @ApiOperation(value = "Update feature toggle", response = FeatureToggleDto.class, httpMethod = "PUT")
    public ResponseEntity<Void> updateFeatureToggle(
        @RequestHeader(value = "Authorization") String bearerToken,
        @RequestBody FeatureToggleValueDto featureToggleDto) {
        if (!isValidToken(bearerToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        featureToggleService.updateFeatureToggle(featureToggleDto);

        return ResponseEntity.ok().build();
    }

    private boolean isValidToken(String token) {
        return "Bearer uaBRA3crTzVyhyPJg28lc7ndutoxPc".equals(token);
    }
}
