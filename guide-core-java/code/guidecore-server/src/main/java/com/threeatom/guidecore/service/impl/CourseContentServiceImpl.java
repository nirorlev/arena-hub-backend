package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.CourseContentMapper;
import com.threeatom.guidecore.service.CourseContentService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Transactional
public class CourseContentServiceImpl extends ServiceImpl<CourseContentMapper, CourseContent>
    implements CourseContentService {

    @Override
    public void saveCourseContents(List<GcVideo> videos) {
        if (CollectionUtils.isEmpty(videos)) {
            return;
        }

        List<CourseContent> content = videos.stream()
            .map(this::createCourseContent)
            .collect(Collectors.toList());

        saveBatch(content);
    }

    @Override
    public void saveCourseContent(GcVideo video) {
        save(createCourseContent(video));
    }

    @Override
    public List<CourseContent> findCourseContent(Integer courseId) {
        return baseMapper.findCourseContent(courseId);
    }

    private CourseContent createCourseContent(GcVideo video) {
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(video.getSubId());
        courseContent.setContentId(video.getId());
        courseContent.setOrder(video.getOrder());
        return courseContent;
    }
}
