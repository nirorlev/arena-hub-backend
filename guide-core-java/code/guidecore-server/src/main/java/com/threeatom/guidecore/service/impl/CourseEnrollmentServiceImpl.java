package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.common.exception.ValidationException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.request.UpdateEnrollmentDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentsDto;
import com.threeatom.guidecore.dto.response.CourseEnrolmentDto;
import com.threeatom.guidecore.dto.response.CourseTotalProgressDto;
import com.threeatom.guidecore.dto.response.UserCourseEnrollmentDto;
import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.CourseEnrollmentProgress;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    @Transactional
    public UserCourseEnrollmentDto enrollToCourse(Integer courseId, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException("Course with specified id not found");
        }
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            throw new ForbiddenException("No permission to view this course");
        }
        Optional<CourseEnrollment> existingCourseEnrollment = findActiveCourseEnrollment(courseId, portalUser);
        if (existingCourseEnrollment.isPresent() && existingCourseEnrollment.get().getEndDate() == null) {
            updateCourseEnrollment(existingCourseEnrollment.get().getId(), getUpdateEnrollmentDto(false), portalUser);
        }

        CourseEnrollment courseEnrollment = createCourseEnrollment(portalUser, courseId);
        save(courseEnrollment);
        return createUserCourseEnrollmentDto(courseEnrollment);
    }

    @Override
    public Optional<CourseEnrollment> findActiveCourseEnrollment(Integer courseId, PortalUser portalUser) {
        return Optional.ofNullable(baseMapper.findCourseEnrollment(courseId, portalUser.getUserId(), true));
    }

    @Override
    public CourseEnrollmentsDto courseEnrollments(Integer courseId, String usersFlag, OffsetDateTime startDate,
                                                  OffsetDateTime endDate, PortalUser portalUser) {
        return courseEnrollmentsDto(getCourseEnrollments(courseId, usersFlag, startDate, endDate, portalUser));
    }

    @Override
    public CourseEnrollmentsDto courseEnrollments(String usersFlag, OffsetDateTime startDate, OffsetDateTime endDate,
                                                  PortalUser portalUser) {
        return courseEnrollmentsDto(getCourseEnrollments(usersFlag, startDate, endDate, portalUser));
    }

    @Override
    public UserCourseEnrollmentDto courseEnrollment(Long enrollmentId, PortalUser portalUser) {
        CourseEnrollment enrollment = getEnrollmentById(enrollmentId);
        return createUserCourseEnrollmentDto(enrollment);
    }

    @Override
    @Transactional
    public UserCourseEnrollmentDto updateCourseEnrollment(Long enrollmentId, UpdateEnrollmentDto updateEnrollmentDto,
                                                          PortalUser portalUser) {
        CourseEnrollment courseEnrollment = getEnrollmentById(enrollmentId);
        if (!courseEnrollment.getUserId().equals(portalUser.getUserId())) {
            log.error("Failed to update course enrollment with id {}. User id {} is not an owner of enrollment",
                enrollmentId, portalUser.getUserId());
            throw new ValidationException("Failed to update course enrollment. User is not an owner of enrollment");
        }

        updateEnrollment(courseEnrollment, updateEnrollmentDto);
        return createUserCourseEnrollmentDto(courseEnrollment);
    }

    @Override
    public CourseEnrollment getActiveEnrollment(Integer courseId, Integer userId) {
        Optional<CourseEnrollment> activeEnrollment = findActiveEnrollment(courseId, userId);
        if (activeEnrollment.isEmpty()) {
            throw new ValidationException("User does not have an active enrollment for this course");
        }

        return activeEnrollment.get();
    }

    private Optional<CourseEnrollment> findActiveEnrollment(Integer courseId, Integer userId) {
        QueryWrapper<CourseEnrollment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("user_id", userId);
        queryWrapper.isNull("end_date");
        return Optional.ofNullable(getOne(queryWrapper, false));
    }

    private void updateEnrollment(CourseEnrollment courseEnrollment, UpdateEnrollmentDto updateEnrollmentDto) {
        if (updateEnrollmentDto.getIsActive()) {
            courseEnrollment.setEndDate(null);
        } else {
            courseEnrollment.setEndDate(OffsetDateTime.now());
        }

        courseEnrollment.setUpdatedTime(OffsetDateTime.now());
        updateById(courseEnrollment);
    }

    private CourseEnrollment getEnrollmentById(Long id) {
        CourseEnrollment enrollment = baseMapper.getCourseEnrollmentById(id);
        if (enrollment == null) {
            log.error("Cannot find course enrollment using id {}", id);
            throw new ResourceNotFoundException("Course enrollment cannot be found by requested id");
        }

        return enrollment;
    }

    private List<CourseEnrollment> getCourseEnrollments(Integer courseId, String usersFlag, OffsetDateTime startDate,
                                                        OffsetDateTime endDate, PortalUser portalUser) {
        if ("all".equals(usersFlag)) {
            return baseMapper.getCourseEnrollments(courseId);
        }
        if ("me".equals(usersFlag)) {
            return baseMapper.getCourseEnrollmentsByUserId(courseId, startDate, endDate, portalUser.getUserId());
        }

        log.error("Invalid users parameter: {}", usersFlag);
        throw new ValidationException("Invalid users parameter");
    }

    private List<CourseEnrollment> getCourseEnrollments(String usersFlag, OffsetDateTime startDate,
                                                        OffsetDateTime endDate, PortalUser portalUser) {
        if ("all".equals(usersFlag)) {
            return baseMapper.getCourseEnrollmentsByMasterId(startDate, endDate, portalUser.getMasterId());
        }
        if ("me".equals(usersFlag)) {
            return baseMapper.getCourseEnrollmentsByUserAndMasterId(startDate, endDate, portalUser.getUserId(),
                portalUser.getMasterId());
        }

        log.error("Invalid users parameter: {}", usersFlag);
        throw new ValidationException("Invalid users parameter");
    }

    private CourseEnrollmentsDto courseEnrollmentsDto(List<CourseEnrollment> courseEnrollments) {
        CourseEnrollmentsDto courseEnrollmentsDto = new CourseEnrollmentsDto();
        courseEnrollmentsDto.setUsers(enrollmentUsers(courseEnrollments));
        courseEnrollmentsDto.setCourses(enrollmentCourseDetails(courseEnrollments));
        return courseEnrollmentsDto;
    }

    private Map<String, CourseEnrolmentDto> enrollmentCourseDetails(List<CourseEnrollment> courseEnrollments) {
        Map<String, CourseEnrolmentDto> courseIdToCourseEnrolmentDto = new HashMap<>();
        Map<Integer, List<CourseEnrollment>> courseIdToCourseEnrollments = courseEnrollments.stream()
            .collect(Collectors.groupingBy(CourseEnrollment::getCourseId));

        for (Map.Entry<Integer, List<CourseEnrollment>> courseIdToCourseEnrollmentsEntry : courseIdToCourseEnrollments.entrySet()) {
            courseIdToCourseEnrolmentDto.put(String.valueOf(courseIdToCourseEnrollmentsEntry.getKey()),
                createCourseEnrollmentDto(courseIdToCourseEnrollmentsEntry.getValue()));
        }

        return courseIdToCourseEnrolmentDto;
    }

    private Map<String, UserDetailsDto> enrollmentUsers(List<CourseEnrollment> courseEnrollments) {
        Set<Integer> uniqueUsers = new HashSet<>();
        List<GcUser> users = courseEnrollments.stream()
            .filter(courseEnrollment -> uniqueUsers.add(courseEnrollment.getUserId()))
            .map(CourseEnrollment::getUser)
            .collect(Collectors.toList());

        return userMapping.map(users).stream()
            .collect(Collectors.toMap(details -> String.valueOf(details.getId()), Function.identity()));
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
            .map(this::createUserCourseEnrollmentDto)
            .collect(Collectors.toList());
    }

    private UserCourseEnrollmentDto createUserCourseEnrollmentDto(CourseEnrollment courseEnrollment) {
        UserCourseEnrollmentDto userCourseEnrollmentDto = new UserCourseEnrollmentDto();

        userCourseEnrollmentDto.setId(courseEnrollment.getId());
        userCourseEnrollmentDto.setStartDate(courseEnrollment.getStartDate());
        userCourseEnrollmentDto.setEndDate(courseEnrollment.getEndDate());
        userCourseEnrollmentDto.setComplianceDate(courseEnrollment.getComplianceDate());
        userCourseEnrollmentDto.setCompletionDate(courseEnrollment.getCompletionDate());

        Optional<CourseEnrollmentProgress> courseProgress = courseEnrollment.getLatestProgress();
        courseProgress.ifPresent(progress -> {
            CourseTotalProgressDto courseTotalProgressDto =
                courseMapping.map(progress, courseEnrollment.getComplianceDate() != null);
            userCourseEnrollmentDto.setProgress(courseTotalProgressDto);
            userCourseEnrollmentDto.setProgressDate(progress.getUpdatedTime());
        });

        return userCourseEnrollmentDto;
    }

    private CourseEnrollment createCourseEnrollment(PortalUser portalUser, Integer courseId) {
        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setCourseId(courseId);
        courseEnrollment.setUserId(portalUser.getUserId());
        courseEnrollment.setStartDate(OffsetDateTime.now());

        return courseEnrollment;
    }

    private UpdateEnrollmentDto getUpdateEnrollmentDto(boolean isActive) {
        UpdateEnrollmentDto updateEnrollmentDto = new UpdateEnrollmentDto();
        updateEnrollmentDto.setIsActive(isActive);
        return updateEnrollmentDto;
    }
}
