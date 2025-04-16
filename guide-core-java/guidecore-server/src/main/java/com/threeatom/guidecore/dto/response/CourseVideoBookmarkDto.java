package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.CourseContentType;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseVideoBookmarkDto {
    private Integer contentId;
    private CourseContentType type;
    private OffsetDateTime bookmarkDate;
    private Integer offset;
}
