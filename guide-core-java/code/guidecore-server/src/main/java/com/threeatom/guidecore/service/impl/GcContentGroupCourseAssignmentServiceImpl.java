package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.enums.UserGroupRole;
import com.threeatom.guidecore.mapper.GcContentGroupCourseAssignmentMapper;
import com.threeatom.guidecore.mapping.GcContentGroupCourseAssignmentMapping;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GcSubjectService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class GcContentGroupCourseAssignmentServiceImpl
    extends ServiceImpl<GcContentGroupCourseAssignmentMapper, GcContentGroupCourseAssignment>
    implements GcContentGroupCourseAssignmentService {

    private static final int OPTIONAL_COURSE_VALUE = 0;
    private static final int MANDATORY_COURSE_VALUE = 1;

    private final GcContentGroupCourseAssignmentMapping gcContentGroupCourseAssignmentMapping;
    @Lazy
    @Autowired
    private GcSubjectService courseService;

    @Override
    public List<ContentGroupCourseAssignmentDto> deprecatedFindByContentGroupId(Integer contentGroupId) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.findByContentGroupId(contentGroupId);

        if (CollectionUtils.isEmpty(contentGroupCourseAssignments)) {
            return new ArrayList<>();
        }

        return contentGroupCourseAssignments.stream()
            .map(contentGroupCourseAssignment -> {
                courseService.updateUrls(contentGroupCourseAssignment.getCourse());
                return gcContentGroupCourseAssignmentMapping.mapDeprecated(contentGroupCourseAssignment);
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupCourseAssignmentDto> findByContentGroupId(Integer contentGroupId) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.findByContentGroupId(contentGroupId);

        if (CollectionUtils.isEmpty(contentGroupCourseAssignments)) {
            return new ArrayList<>();
        }

        return contentGroupCourseAssignments.stream()
            .map(contentGroupCourseAssignment -> {
                courseService.updateUrls(contentGroupCourseAssignment.getCourse());
                return gcContentGroupCourseAssignmentMapping.map(contentGroupCourseAssignment);
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getCourseIdsByContentGroupId(Integer contentGroupId) {
        return getCourseIdsByContentGroupIdAndPredicate(contentGroupId, assignment -> true);
    }

    @Override
    public void assignCourse(AssignCourseDto assignCourseDto, Integer userId) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment =
            gcContentGroupCourseAssignmentMapping.map(assignCourseDto, userId);

        save(contentGroupCourseAssignment);
    }

    @Override
    public void assignCourse(Integer contentGroupId, AssignCourseDto assignCourseDto, Integer userId) {
        assignCourseDto.setContentGroupId(contentGroupId);
        assignCourse(assignCourseDto, userId);
    }

    private void updateCourseAssignment(Integer courseAssignmentId, Integer userId, AssignCourseDto assignCourseDto) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment = getById(courseAssignmentId);

        gcContentGroupCourseAssignmentMapping.update(contentGroupCourseAssignment, assignCourseDto, userId);
        updateById(contentGroupCourseAssignment);
    }

    @Override
    public void updateCourseAssignmentMandatoryOpposite(Integer courseId, Integer contentGroupId) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment =
            this.baseMapper.findByCourseIdAndContentGroupId(courseId, contentGroupId);

        if (contentGroupCourseAssignment != null) {
            contentGroupCourseAssignment.setMandatory(
                getMandatoryOppositeValue(contentGroupCourseAssignment.getMandatory()));
            updateById(contentGroupCourseAssignment);
        }
    }

    @Override
    public void save(GcUser user, GcSubject course, CourseType type) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            new ArrayList<>(createCoursesAssignment(user, course, type));

        saveBatch(contentGroupCourseAssignments);
    }

    @Override
    public void save(GcUser user, List<Integer> courseIds, Integer contentGroupId, CourseType type) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments = courseIds.stream()
            .map(courseId -> createContentGroupCourseAssignment(user, courseId, contentGroupId, type))
            .collect(Collectors.toList());

        saveBatch(contentGroupCourseAssignments);
    }

    @Override
    public List<Integer> getMustCoursesContentGroupAssignmentIds(Integer contentGroupId) {
        return getCourseIdsByContentGroupIdAndPredicate(contentGroupId, assignment -> assignment.getMandatory() ==
            MANDATORY_COURSE_VALUE);
    }

    @Override
    public List<Integer> getOptionalCoursesContentGroupAssignmentIds(Integer contentGroupId) {
        return getCourseIdsByContentGroupIdAndPredicate(contentGroupId, assignment -> assignment.getMandatory() ==
            OPTIONAL_COURSE_VALUE);
    }

    @Override
    public void removeCourseAssignmentsByCourseId(List<Integer> courseIds, Integer contentGroupId) {
        this.baseMapper.removeByContentGroupIdAndCourseIds(contentGroupId, courseIds);
    }

    @Override
    public void removeByMasterAndCourseId(Integer masterId, Integer courseId) {
        this.baseMapper.removeByMasterAndCourseId(masterId, courseId);
    }

    @Override
    public List<Integer> getMustCoursesContentGroupAssignmentIds(Integer userId, Integer masterId) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.getCoursesContentGroupAssignmentByUserAndMasterId(userId, masterId);

        return filterCourseIdsByPredicate(contentGroupCourseAssignments, assignment -> assignment.getMandatory() ==
            MANDATORY_COURSE_VALUE);
    }

    @Override
    public List<Integer> getOptionalCoursesContentGroupAssignmentIds(Integer userId, Integer masterId) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.getCoursesContentGroupAssignmentByUserAndMasterId(userId, masterId);

        return filterCourseIdsByPredicate(contentGroupCourseAssignments,
            assignment -> assignment.getMandatory() == OPTIONAL_COURSE_VALUE);
    }

    @Override
    public List<Integer> getMustCourseIds(Integer userId, Integer masterId, UserGroupRole userRole) {
        List<GcContentGroupCourseAssignment> courseAssignments =
            this.baseMapper.getCourseAssignmentsByRoleUserAndMasterId(userId, masterId, userRole.getRole());

        return filterCourseIdsByPredicate(
            courseAssignments, assignment -> assignment.getMandatory() == MANDATORY_COURSE_VALUE);
    }

    @Override
    public Set<Integer> getContentGroupIds(Integer courseId) {
        QueryWrapper<GcContentGroupCourseAssignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);

        List<GcContentGroupCourseAssignment> assignments = list(queryWrapper);

        return assignments.stream()
            .map(GcContentGroupCourseAssignment::getContentGroupId)
            .collect(Collectors.toSet());
    }

    @Override
    public void assignOrUpdateCourse(Integer contentGroupId, AssignCourseDto assignCourseDto, Integer userId) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment =
            this.baseMapper.findByCourseIdAndContentGroupId(assignCourseDto.getCourseId(), contentGroupId);

        if (contentGroupCourseAssignment == null) {
            assignCourse(contentGroupId, assignCourseDto, userId);
            return;
        }

        updateCourseAssignment(contentGroupCourseAssignment.getId(), userId, assignCourseDto);
    }

    private List<Integer> getCourseIdsByContentGroupIdAndPredicate(
        Integer contentGroupId, Predicate<GcContentGroupCourseAssignment> assignmentPredicate) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.findByContentGroupId(contentGroupId);

        return filterCourseIdsByPredicate(contentGroupCourseAssignments, assignmentPredicate);
    }

    private List<Integer> filterCourseIdsByPredicate(
        List<GcContentGroupCourseAssignment> assignments,
        Predicate<GcContentGroupCourseAssignment> assignmentPredicate) {
        if (CollectionUtils.isEmpty(assignments)) {
            return new ArrayList<>();
        }

        return assignments.stream()
            .filter(Objects::nonNull)
            .filter(assignmentPredicate)
            .map(GcContentGroupCourseAssignment::getCourseId)
            .collect(Collectors.toList());
    }

    private List<GcContentGroupCourseAssignment> createCoursesAssignment(
        GcUser user, GcSubject course, CourseType type) {
        List<Integer> courseIds = type.isMandatory() ? course.getMustAccessIds() : course.getAccessIds();

        if (CollectionUtils.isEmpty(courseIds)) {
            return new ArrayList<>();
        }

        return courseIds.stream()
            .map(contentGroupId -> createContentGroupCourseAssignment(user, course.getId(), contentGroupId, type))
            .collect(Collectors.toList());
    }

    private GcContentGroupCourseAssignment createContentGroupCourseAssignment(
        GcUser user, int courseId, Integer contentGroupId, CourseType courseType) {
        GcContentGroupCourseAssignment gcContentGroupCourseAssignment = new GcContentGroupCourseAssignment();

        gcContentGroupCourseAssignment.setCreatedByUserId(user.getId());
        gcContentGroupCourseAssignment.setContentGroupId(contentGroupId);
        gcContentGroupCourseAssignment.setCourseId(courseId);
        gcContentGroupCourseAssignment.setMandatory(getMandatoryOppositeValue(courseType.getValue()));

        return gcContentGroupCourseAssignment;
    }

    private int getMandatoryOppositeValue(int contentGroupCourseAssignment) {
        if (contentGroupCourseAssignment == MANDATORY_COURSE_VALUE) {
            return OPTIONAL_COURSE_VALUE;
        }

        return MANDATORY_COURSE_VALUE;
    }

}
