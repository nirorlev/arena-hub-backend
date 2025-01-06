package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.Api;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Api("Data Transfer Object representing a video with details")
public class VideoWithSourceDetailsDto<T> extends VideoWithDetailsDto {
    private T origin;
}
