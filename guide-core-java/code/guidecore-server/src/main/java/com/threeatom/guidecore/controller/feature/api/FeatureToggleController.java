package com.threeatom.guidecore.controller.feature.api;

import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/api/v2/app-config",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Feature Toggle", produces = MediaType.APPLICATION_JSON_VALUE)
public class FeatureToggleController {

    private final FeatureToggleService featureToggleService;

    @GetMapping
    @ApiOperation(value = "Get all feature toggles", response = FeatureToggleDto.class, httpMethod = "GET")
    public ResponseEntity<FeatureToggleDto> getAllFeatureToggles(HttpServletRequest request) {
        Optional<Integer> masterIdOptional = RequestUtil.getMasterId(request);

        return masterIdOptional.map(masterId -> ResponseEntity.ok().body(featureToggleService.getAllFeatures(masterId)))
            .orElseGet(() -> ResponseEntity.ok().body(featureToggleService.getAllDefaultFeatures()));
    }

    @PutMapping
    @ApiOperation(value = "Update feature toggle", response = FeatureToggleDto.class, httpMethod = "PUT")
    public ResponseEntity<FeatureToggleDto> updateFeatureToggle(
        @RequestHeader(value = "Authorization") String bearerToken,
        @RequestBody FeatureToggleValueDto featureToggleDto) {
        if (!isValidToken(bearerToken)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok().body(featureToggleService.updateFeatureToggle(featureToggleDto));
    }

    private boolean isValidToken(String token) {
        return "Bearer uaBRA3crTzVyhyPJg28lc7ndutoxPc".equals(token);
    }
}
