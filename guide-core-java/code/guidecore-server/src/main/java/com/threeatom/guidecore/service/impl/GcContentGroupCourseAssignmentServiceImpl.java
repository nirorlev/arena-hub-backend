package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.mapper.GcContentGroupCourseAssignmentMapper;
import com.threeatom.guidecore.mapping.GcContentGroupCourseAssignmentMapping;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class GcContentGroupCourseAssignmentServiceImpl
    extends ServiceImpl<GcContentGroupCourseAssignmentMapper, GcContentGroupCourseAssignment>
    implements GcContentGroupCourseAssignmentService {

    private final GcContentGroupCourseAssignmentMapping gcContentGroupCourseAssignmentMapping;

    @Override
    @Transactional(readOnly = true)
    public List<ContentGroupCourseAssignmentDto> findByContentGroupId(Integer contentGroupId) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments =
            this.baseMapper.findByContentGroupId(contentGroupId);

        if (CollectionUtils.isEmpty(contentGroupCourseAssignments)) {
            return Collections.emptyList();
        }

        return contentGroupCourseAssignments.stream()
            .map(gcContentGroupCourseAssignmentMapping::map)
            .collect(Collectors.toList());
    }

    @Override
    public void assignCourse(GcUser currentUser, AssignCourseDto assignCourseDto) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment =
            gcContentGroupCourseAssignmentMapping.map(assignCourseDto, currentUser.getId());

        save(contentGroupCourseAssignment);
    }

    @Override
    public void updateCourseAssignment(Integer courseAssignmentId, AssignCourseDto assignCourseDto) {
        GcContentGroupCourseAssignment contentGroupCourseAssignment = getById(courseAssignmentId);

        gcContentGroupCourseAssignmentMapping.update(contentGroupCourseAssignment, assignCourseDto);
        updateById(contentGroupCourseAssignment);
    }

    @Override
    public void removeCourseAssignment(Integer courseAssignmentId) {
        removeById(courseAssignmentId);
    }

    @Override
    public void save(GcUser user, GcSubject course) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments = new ArrayList<>();

        contentGroupCourseAssignments.addAll(createCoursesAssignment(user, course, CourseType.MANDATORY));
        contentGroupCourseAssignments.addAll(createCoursesAssignment(user, course, CourseType.OPTIONAL));

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
    public void assignCourses(GcUser currentUser, List<AssignCourseDto> assignCourseDtos) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments = assignCourseDtos.stream()
            .map(assignCourseDto -> gcContentGroupCourseAssignmentMapping.map(assignCourseDto, currentUser.getId()))
            .collect(Collectors.toList());

        saveBatch(contentGroupCourseAssignments);
    }

    private List<GcContentGroupCourseAssignment> createCoursesAssignment(GcUser user, GcSubject course, CourseType type) {
        List<Integer> courseIds = type.isMandatory() ? course.getMustAccessIds() : course.getAccessIds();

        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
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
        gcContentGroupCourseAssignment.setMandatory(courseType.isMandatory());

        return gcContentGroupCourseAssignment;
    }

}
