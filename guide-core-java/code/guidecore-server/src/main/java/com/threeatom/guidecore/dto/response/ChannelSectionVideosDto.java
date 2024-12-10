package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChannelSectionVideosDto {
    private String name;
    private List<VideoWithDetailsDto> videos;
}
