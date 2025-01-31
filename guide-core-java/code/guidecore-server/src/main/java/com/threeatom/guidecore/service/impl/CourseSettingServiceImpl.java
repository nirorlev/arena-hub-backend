package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.threeatom.guidecore.mapping.CourseMapping;
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
    private final CourseMapping courseMapping;

    @Override
    public void save(Integer courseId, CourseSettingDto courseSettingDto, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.EDIT, portalUser)) {
            throw new ForbiddenException("No permission to edit this course");
        }

        CourseSetting courseSetting = courseMapping.mapToSetting(courseSettingDto, courseId);
        save(courseSetting);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseSetting findByCourseId(Integer courseId) {
        QueryWrapper<CourseSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        return getOne(queryWrapper);
    }

    @Override
    public void update(Integer courseId, CourseSettingDto courseSettingDto, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.EDIT, portalUser)) {
            throw new ForbiddenException("No permission to edit this course");
        }

        CourseSetting courseSetting = findByCourseId(courseId);
        courseMapping.mapToUpdateSetting(courseSetting, courseSettingDto);
        updateById(courseSetting);
    }

}
