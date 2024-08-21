package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseUsageDto {
    private Integer privateChannelCount;
    private Integer privatePlaylistCount;
    private Integer publishedChannelCount;
    private Integer publishedPlaylistCount;
}
