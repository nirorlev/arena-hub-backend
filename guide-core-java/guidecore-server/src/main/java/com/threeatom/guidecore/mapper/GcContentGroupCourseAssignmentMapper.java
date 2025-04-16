package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcContentGroupCourseAssignmentMapper
    extends BaseMapper<GcContentGroupCourseAssignment> {

    List<GcContentGroupCourseAssignment> findByContentGroupId(
        @Param("contentGroupId") Integer contentGroupId);

    GcContentGroupCourseAssignment findByCourseIdAndContentGroupId(
        @Param("contentGroupId") Integer contentGroupId, @Param("courseId") Integer courseId);

    void removeByContentGroupIdAndCourseIds(
        @Param("contentGroupId") Integer contentGroupId, @Param("courseIds") List<Integer> courseIds);

    void removeByMasterAndCourseId(@Param("masterId") Integer masterId, @Param("courseId") Integer courseId);

    List<GcContentGroupCourseAssignment> getCoursesContentGroupAssignmentByUserAndMasterId(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<GcContentGroupCourseAssignment> getCourseAssignmentsByRoleUserAndMasterId(
        @Param("userId") Integer userId, @Param("masterId") Integer masterId, @Param("roleName") String roleName);
}
