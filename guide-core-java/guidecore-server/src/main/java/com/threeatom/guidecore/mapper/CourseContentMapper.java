package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.CourseContent;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CourseContentMapper extends BaseMapper<CourseContent> {
    List<CourseContent> findCourseContent(@Param("courseId") Integer courseId);
}
