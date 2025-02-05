package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.CourseSettingMapper;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseSettingServiceImpl extends ServiceImpl<CourseSettingMapper, CourseSetting>
    implements CourseSettingService {

    private final AuthorizationService authorizationService;
    private final GcSubjectService courseService;

    @Override
    public void save(Integer courseId, CourseSettingDto courseSetting, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.EDIT, portalUser)) {
            throw new ForbiddenException("No permission to edit this course");
        }

        save(createSetting(courseId, courseSetting));
    }

    private CourseSetting createSetting(Integer courseId, CourseSettingDto courseSetting) {
        CourseSetting courseSettingEntity = new CourseSetting();
        courseSettingEntity.setCourseId(courseId);
        courseSettingEntity.setCourseContentStudyPercentage(courseSetting.getCourseContentStudyPercentage());
        courseSettingEntity.setSingleVideoViewPercentage(courseSetting.getSingleVideoViewPercentage());
        return courseSettingEntity;
    }
}
