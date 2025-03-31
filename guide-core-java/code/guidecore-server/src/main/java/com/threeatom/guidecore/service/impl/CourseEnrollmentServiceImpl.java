package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.CourseEnrolmentDto;
import com.threeatom.guidecore.dto.response.CourseTotalProgressDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.CourseProgress;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.CourseEnrollmentMapper;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.mapping.UserMapping;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.GcSubjectService;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseEnrollmentServiceImpl extends ServiceImpl<CourseEnrollmentMapper, CourseEnrollment> implements
    CourseEnrollmentService {

    private final GcSubjectService courseService;
    private final UserMapping userMapping;
    private final CourseMapping courseMapping;
    private final AuthorizationService authorizationService;

    @Override
    public void enrollToCourse(PortalUser portalUser, Integer courseId) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("No permission to view this course");
        }

        CourseEnrollment courseEnrollment = createCourseUser(portalUser, courseId);

        save(courseEnrollment);
    }

    @Override
    public Optional<CourseEnrollment> findCourseEnrollment(Integer courseId, PortalUser portalUser) {
        return Optional.ofNullable(baseMapper.findCourseEnrollment(courseId));
    }

    @Override
    public CourseEnrollmentsDto courseEnrollments(Integer courseId, String usersFlag, OffsetDateTime startDate,
                                                  OffsetDateTime endDate, PortalUser portalUser) {
        List<CourseEnrollment> courseEnrollments = getCourseEnrollments(courseId, usersFlag, startDate, endDate, portalUser);
        List<GcUser> users = courseEnrollments.stream()
            .map(CourseEnrollment::getUser)
            .collect(Collectors.toList());

        CourseEnrollmentsDto courseEnrollmentsDto = new CourseEnrollmentsDto();
        courseEnrollmentsDto.setUsers(userMapping.map(users));

        Map<String, CourseEnrolmentDto> courseIdToCourseEnrolmentDto = new HashMap<>();
        Map<Integer, List<CourseEnrollment>> courseIdToCourseEnrollments = courseEnrollments.stream()
            .collect(Collectors.groupingBy(CourseEnrollment::getCourseId));

        for (Map.Entry<Integer, List<CourseEnrollment>> courseIdToCourseEnrollmentsEntry : courseIdToCourseEnrollments.entrySet()) {
            courseIdToCourseEnrolmentDto.put(String.valueOf(courseIdToCourseEnrollmentsEntry.getKey()),
                createCourseEnrollmentDto(courseIdToCourseEnrollmentsEntry.getValue()));
        }

        courseEnrollmentsDto.setCourses(courseIdToCourseEnrolmentDto);
        return courseEnrollmentsDto;
    }

    private List<CourseEnrollment> getCourseEnrollments(Integer courseId, String usersFlag, OffsetDateTime startDate,
                                                        OffsetDateTime endDate, PortalUser portalUser) {
        if ("all".equals(usersFlag)) {
            return baseMapper.getCourseEnrollments(courseId);
        }
        return baseMapper.getCourseEnrollmentsByUserId(courseId, startDate, endDate, portalUser.getUserId());
    }

    private CourseEnrolmentDto createCourseEnrollmentDto(List<CourseEnrollment> courseEnrollments) {
        GcSubject course = courseEnrollments.get(0).getCourse();
        CourseEnrolmentDto courseEnrolmentDto = courseMapping.mapCourseEnrollment(course);

        Map<Integer, List<CourseEnrollment>> userIdToCourseEnrollments = courseEnrollments.stream()
            .collect(Collectors.groupingBy(CourseEnrollment::getUserId));
        Map<String, List<UserCourseEnrollmentDto>> userIdToCourseEnrollmentsDto = new HashMap<>();

        for (Map.Entry<Integer, List<CourseEnrollment>> userIdToCourseEnrollmentsEntry : userIdToCourseEnrollments.entrySet()) {
            List<CourseEnrollment> userCourseEnrollments = userIdToCourseEnrollmentsEntry.getValue();
            userIdToCourseEnrollmentsDto.put(String.valueOf(userIdToCourseEnrollmentsEntry.getKey()),
                createUserCourseEnrollmentsDto(userCourseEnrollments));
        }

        courseEnrolmentDto.setUserEnrollments(userIdToCourseEnrollmentsDto);
        return courseEnrolmentDto;
    }

    private List<UserCourseEnrollmentDto> createUserCourseEnrollmentsDto(List<CourseEnrollment> userCourseEnrollments) {
        return userCourseEnrollments.stream()
            .map(userCourseEnrollment -> {
                UserCourseEnrollmentDto userCourseEnrollmentDto = new UserCourseEnrollmentDto();
                Optional<CourseProgress> courseProgress = userCourseEnrollment.getLatestProgress();

                userCourseEnrollmentDto.setId(userCourseEnrollment.getId());
                userCourseEnrollmentDto.setStartDate(userCourseEnrollment.getStartDate());
                userCourseEnrollmentDto.setEndDate(userCourseEnrollment.getEndDate());
                userCourseEnrollmentDto.setComplianceDate(userCourseEnrollment.getComplianceDate());

                courseProgress.ifPresent(progress -> {
                    CourseTotalProgressDto courseTotalProgressDto =
                        courseMapping.map(progress, userCourseEnrollment.getComplianceDate() != null);
                    userCourseEnrollmentDto.setProgress(courseTotalProgressDto);
                    userCourseEnrollmentDto.setProgressDate(progress.getUpdatedTime());
                });

                return userCourseEnrollmentDto;
            })
            .collect(Collectors.toList());
    }

    private CourseEnrollment createCourseUser(PortalUser portalUser, Integer courseId) {
        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setCourseId(courseId);
        courseEnrollment.setUserId(portalUser.getUserId());

        return courseEnrollment;
    }
}
