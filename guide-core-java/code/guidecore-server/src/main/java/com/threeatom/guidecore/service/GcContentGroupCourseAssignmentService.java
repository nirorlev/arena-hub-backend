package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.CourseType;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import java.util.List;

public interface GcContentGroupCourseAssignmentService
        extends IService<GcContentGroupCourseAssignment> {
    List<ContentGroupCourseAssignmentDto> findByContentGroupId(Integer contentGroupId);

    void assignCourse(AssignCourseDto assignCourseDto);

    void save(GcUser user, GcSubject course);

    void save(GcUser user, List<Integer> idList, Integer contentGroupId, CourseType type);

    void assignCourses(List<AssignCourseDto> assignCourseDto);
}
