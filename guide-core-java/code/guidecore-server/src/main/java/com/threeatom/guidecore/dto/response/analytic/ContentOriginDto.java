package com.threeatom.guidecore.dto.response.analytic;

import com.threeatom.guidecore.enums.ContentOrigin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentOriginDto {
    private Integer id;
    private Integer contentId;
    private ContentOrigin type;
    private String title;
    private String channelSlug;
}
