package com.threeatom.guidecore.controller.feature.api;

import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.service.FeatureToggleService;
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
public class FeatureToggleController {

    private final FeatureToggleService featureToggleService;

    @GetMapping
    public ResponseEntity<FeatureToggleDto> getAllFeatureToggles() {
        return ResponseEntity.ok(featureToggleService.getAllFeatures());
    }
}
