package com.threeatom.guidecore.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LicenseUsageDto {
    private int privateChannelCount;
    private int privatePlaylistCount;
    private int publishedChannelCount;
    private int publishedPlaylistCount;
}
