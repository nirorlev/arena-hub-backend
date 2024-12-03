package com.threeatom.guidecore.controller.version.api;

import com.threeatom.guidecore.dto.response.VersionOverrideDto;
import com.threeatom.guidecore.service.FeVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "/api/v2/admin/version-override",
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Api(tags = "Version Override", produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionOverrideController {

    private final FeVersionService feVersionService;

    @GetMapping
    @ApiOperation(value = "Get Latest version from DB", response = VersionOverrideDto.class, httpMethod = "GET")
    public ResponseEntity<VersionOverrideDto> getLatestDbVersionOverride() {
        String latestVersion = feVersionService.findLatestVersion();
        return ResponseEntity.ok(VersionOverrideDto.builder().version(latestVersion).build());
    }

    @PostMapping
    @ApiOperation(value = "Update Latest FE version", response = VersionOverrideDto.class, httpMethod = "POST")
    public ResponseEntity<Void> updateLatestFeVersion(@RequestBody @Valid VersionOverrideDto versionOverrideDto) {
        feVersionService.saveVersion(versionOverrideDto.getVersion());
        return ResponseEntity.ok().build();
    }
}
