package com.threeatom.guidecore.controller.feature.api;

import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.GcManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final GcManagerService managerService;

    @GetMapping
    @ApiOperation(value = "Get all feature toggles", response = FeatureToggleDto.class, httpMethod = "GET")
    public ResponseEntity<FeatureToggleDto> getAllFeatureToggles(HttpServletRequest request) {
        GcManager currentManager = managerService.getCurrentManager(request);

        if (currentManager != null) {
            return ResponseEntity.ok().body(featureToggleService.getAllFeatures(currentManager));
        }

        return ResponseEntity.ok().body(featureToggleService.getAllFeatures());
    }
}
