package com.threeatom.guidecore.controller.feature.api;

import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/api/v2/app-config",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class featureToggleController {

    @GetMapping
    private ResponseEntity<FeatureToggleDto> getAllFeatureToggles() {
        FeatureToggleDto featureToggleDto = new FeatureToggleDto();
        featureToggleDto.setFeatures(
            Map.of("courseDeadlines", "enabled", "courses", "hide", "playerPageNotes", "disabled"));
        return ResponseEntity.ok(featureToggleDto);
    }
}
