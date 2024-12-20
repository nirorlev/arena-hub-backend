package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@ApiModel(description = "Data Transfer Object representing a version override")
public class VersionOverrideDto {
    @NotBlank
    private String version;
}
