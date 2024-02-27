package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.entity.GcContentGroupCourseAssignment;
import java.util.List;

public interface GcContentGroupCourseAssignmentService
        extends IService<GcContentGroupCourseAssignment> {
    List<ContentGroupCourseAssignmentDto> findByContentGroupId(Integer contentGroupId);
}
