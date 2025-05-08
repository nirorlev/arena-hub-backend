package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    @Transactional(readOnly = true)
    public List<CourseContent> findCourseContent(Integer courseId) {
        return baseMapper.findCourseContent(courseId);
    }

    @Override
    @Transactional
    public void updateOrder(List<Integer> videoIds) {
        List<CourseContent> courseContent = getVideoContentByIds(videoIds);
        List<CourseContent> updatedContent = courseContent.stream()
            .filter(content -> videoIds.contains(content.getContentId()))
            .peek(content -> content.setOrder(videoIds.indexOf(content.getContentId())))
            .collect(Collectors.toList());

        updateBatchById(updatedContent);
    }

    private List<CourseContent> getVideoContentByIds(List<Integer> videoIds) {
        QueryWrapper<CourseContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("content_id", videoIds);
        return list(queryWrapper);
    }

    private CourseContent createCourseContent(GcVideo video) {
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(video.getSubId());
        courseContent.setContentId(video.getId());
        courseContent.setOrder(video.getOrder());
        return courseContent;
    }
}
