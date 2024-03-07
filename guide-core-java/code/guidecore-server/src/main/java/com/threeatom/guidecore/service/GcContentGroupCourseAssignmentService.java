package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import java.util.List;

public interface GcContentGroupCourseAssignmentService
        extends IService<GcContentGroupCourseAssignment> {
    List<ContentGroupCourseAssignmentDto> findByContentGroupId(Integer contentGroupId);

    List<Integer> getCourseIdsByContentGroupId(Integer contentGroupId);

    void assignCourse(GcUser currentUser, AssignCourseDto assignCourseDto);

    void updateCourseAssignment(Integer courseAssignmentId, GcUser currentUser, AssignCourseDto assignCourseDto);

    void updateCourseAssignmentMandatoryOpposite(Integer courseId, Integer contentGroupId);

    void removeCourseAssignment(Integer courseAssignmentId);

    void save(GcUser user, GcSubject course);

    void save(GcUser user, List<Integer> idList, Integer contentGroupId, CourseType type);

    void assignCourses(GcUser currentUser, List<AssignCourseDto> assignCourseDto);

    List<Integer> getMustCoursesContentGroupAssignmentIds(Integer contentGroupId);

    List<Integer> getOptionalCoursesContentGroupAssignmentIds(Integer contentGroupId);

    void removeCourseAssignmentsByCourseId(GcAccess contentGroup, List<Integer> courseIds);

    void removeByMasterAndCourseId(Integer masterId, Integer courseId);
}
