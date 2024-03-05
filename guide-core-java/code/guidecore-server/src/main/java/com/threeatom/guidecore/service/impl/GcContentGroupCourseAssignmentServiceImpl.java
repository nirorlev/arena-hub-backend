package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
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
    @Transactional
    public void assignCourse(AssignCourseDto assignCourseDto) {
        save(gcContentGroupCourseAssignmentMapping.map(assignCourseDto));
    }

    @Override
    public void save(GcUser user, GcSubject course) {
        List<GcContentGroupCourseAssignment> contentGroupCourseAssignments = new ArrayList<>();

        contentGroupCourseAssignments.addAll(createMustTakeCourses(user, course));
        contentGroupCourseAssignments.addAll(createsMayTakeCourses(user, course));

        saveBatch(contentGroupCourseAssignments);
    }

    private List<GcContentGroupCourseAssignment> createMustTakeCourses(GcUser user, GcSubject course) {
        return createCourses(user, course, true);
    }

    private List<GcContentGroupCourseAssignment> createsMayTakeCourses(GcUser user, GcSubject course) {
        return createCourses(user, course, false);
    }

    private List<GcContentGroupCourseAssignment> createCourses(GcUser user, GcSubject course, boolean isMandatory) {
        List<Integer> courseIds = isMandatory ? course.getMustAccessIds() : course.getAccessIds();

        if (CollectionUtils.isEmpty(courseIds)) {
            return Collections.emptyList();
        }

        return courseIds.stream()
            .map(courseId -> createContentGroupCourseAssignment(user, course, courseId, isMandatory))
            .collect(Collectors.toList());
    }

    private GcContentGroupCourseAssignment createContentGroupCourseAssignment(
        GcUser user, GcSubject course, Integer contentGroupId, boolean isMandatory) {
        GcContentGroupCourseAssignment gcContentGroupCourseAssignment = new GcContentGroupCourseAssignment();

        gcContentGroupCourseAssignment.setCreatedByUserId(user.getId());
        gcContentGroupCourseAssignment.setContentGroupId(contentGroupId);
        gcContentGroupCourseAssignment.setCourseId(course.getId());
        gcContentGroupCourseAssignment.setMandatory(isMandatory);

        return gcContentGroupCourseAssignment;
    }

}
