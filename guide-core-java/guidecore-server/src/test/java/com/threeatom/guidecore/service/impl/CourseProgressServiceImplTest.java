package com.threeatom.guidecore.service.impl;

import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.CourseService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoEventService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseProgressServiceImplTest {

    @Mock
    private CourseService courseService;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private CourseMapping courseMapping;
    @Mock
    private VideoPlaySessionService videoPlaySessionService;
    @Mock
    private CourseEnrollmentService courseEnrollmentService;
    @Mock
    private CourseSettingService courseSettingService;
    @Mock
    private VideoEventService videoEventService;
    @Mock
    private UserTaskAnswerService userTaskAnswerService;

    @InjectMocks
    private CourseEnrollmentProgressServiceImpl courseProgressService;

    @Test
    void test() {
    }
}
