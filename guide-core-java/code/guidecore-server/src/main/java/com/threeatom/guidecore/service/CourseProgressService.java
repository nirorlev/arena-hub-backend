package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.entity.CourseProgress;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseProgressService extends IService<CourseProgress> {
    CourseProgressDto courseProgress(Integer courseId, PortalUser portalUser);
}
