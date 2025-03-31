package com.threeatom.guidecore.dto.response;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCourseEnrollmentDto {
    private Long id;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private CourseTotalProgressDto progress;
    private OffsetDateTime progressDate;
    private OffsetDateTime complianceDate;
    private Integer certificateId;
}
