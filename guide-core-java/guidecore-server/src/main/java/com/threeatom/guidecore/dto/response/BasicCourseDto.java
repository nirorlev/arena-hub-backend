package com.threeatom.guidecore.dto.response;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicCourseDto {
    private Integer id;
    private String title;
    private String description;
    private String thumbUrl;
    private Boolean isPrivate;
    private Boolean isPublic;
    private Map<String, Boolean> permissions;
}
