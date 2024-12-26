package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.CourseUser;
import com.threeatom.guidecore.entity.PortalUser;

public interface CourseUserService extends IService<CourseUser> {
    void enrollToCourse(PortalUser portalUser, Integer courseId);
}
