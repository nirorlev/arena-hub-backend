package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseUsageDto {
    private Integer privateChannelCount = -1;
    private Integer privatePlaylistCount = -1;
    private Integer publishedChannelCount = -1;
    private Integer publishedPlaylistCount = -1;
}
