package com.threeatom.guidecore.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoViewerDetailsDto extends DateRangeDto {
    private List<Integer> videoIds = new ArrayList<>();
}
