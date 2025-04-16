package com.threeatom.guidecore.dto.response.analytic;

import com.threeatom.guidecore.dto.response.UserDetailsDto;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoViewerDto {
    private UserDetailsDto user;
    private Map<String, VideoViewerVideoDetailDto> videos = new HashMap<>();
}
