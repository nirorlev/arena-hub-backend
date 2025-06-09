package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.CourseType;
import com.threeatom.guidecore.enums.UserGroupRole;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GcContentGroupCourseAssignmentService extends IService<GcContentGroupCourseAssignment> {
    @Deprecated(forRemoval = true)
    List<ContentGroupCourseAssignmentDto> deprecatedFindByContentGroupId(Integer contentGroupId);

    Map<String, List<GroupCourseAssignmentDto>> findByContentGroupId(Integer contentGroupId);

    List<Integer> getCourseIdsByContentGroupId(Integer contentGroupId);

    void assignCourse(AssignCourseDto assignCourseDto, Integer userId);

    void assignCourse(Integer contentGroupId, AssignCourseDto assignCourseDto, Integer userId);

    void updateCourseAssignmentMandatoryOpposite(Integer courseId, Integer contentGroupId);

    void save(GcUser user, Course course, CourseType type);

    void save(GcUser user, List<Integer> idList, Integer contentGroupId, CourseType type);

    List<Integer> getMustCoursesContentGroupAssignmentIds(Integer contentGroupId);

    List<Integer> getOptionalCoursesContentGroupAssignmentIds(Integer contentGroupId);

    void removeCourseAssignmentsByCourseId(List<Integer> courseIds, Integer contentGroupId);

    List<Integer> getMustCoursesContentGroupAssignmentIds(Integer userId, Integer masterId);

    List<Integer> getOptionalCoursesContentGroupAssignmentIds(Integer userId, Integer masterId);

    List<Integer> getMustCourseIds(Integer userId, Integer masterId, UserGroupRole userRole);

    Set<Integer> getContentGroupIds(Integer courseId);

    void assignOrUpdateCourse(Integer contentGroupId, AssignCourseDto assignCourseDto, Integer userId);

    List<GcContentGroupCourseAssignment> userCourseAssignments(PortalUser portalUser);
}
