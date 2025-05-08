package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.GcVideo;
import java.util.List;

public interface CourseContentService extends IService<CourseContent> {
    void saveCourseContents(List<GcVideo> videos);

    List<CourseContent> findCourseContent(Integer courseId);

    void updateOrder(List<Integer> videoIds);
}
