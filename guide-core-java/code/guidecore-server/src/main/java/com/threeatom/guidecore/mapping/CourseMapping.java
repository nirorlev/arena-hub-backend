package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseSectionContentDto;
import com.threeatom.guidecore.dto.response.CourseSectionDto;
import com.threeatom.guidecore.dto.response.CourseProgressDetailsDto;
import com.threeatom.guidecore.dto.response.ProgressDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.enums.CourseContentType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(uses = {DateMapping.class, OwnerMapping.class})
public interface CourseMapping {

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "avatarUrl", source = "subImgFile.fullFileUrl")
    VideoSourceDto map(GcSubject course);

    CourseProgressDetailsDto mapToCourseProgress(GcSubject course, Double progress, CourseSetting compliance);

    ProgressDto mapToProgress(Double progress);

    CourseSettingDto mapToSetting(CourseSetting courseSetting);

    @Mapping(target = "id", source = "course.id")
    @Mapping(target = "name", source = "course.name")
    @Mapping(target = "sections", source = "courseContent", qualifiedByName = "mapCourseSectionContent")
    CourseProgramDto mapProgram(GcSubject course, List<CourseContent> courseContent);

    @Named("mapCourseSectionContent")
    default List<CourseSectionDto> mapCourseSectionContent(List<CourseContent> courseContent) {
        if (courseContent == null) {
            return null;
        }

        Map<Integer, List<CourseContent>> courseToContent =
            courseContent.stream().collect(Collectors.groupingBy(CourseContent::getCourseId));

        return courseToContent.values().stream()
            .map(this::mapCourseSection)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private CourseSectionDto mapCourseSection(List<CourseContent> courseContent) {
        if (CollectionUtils.isEmpty(courseContent) || courseContent.get(0).getCourse() == null) {
            return null;
        }

        CourseSectionDto sectionDto = new CourseSectionDto();
        GcSubject section = courseContent.get(0).getCourse();

        sectionDto.setId(section.getId());
        sectionDto.setName(section.getName());

        List<CourseSectionContentDto> sectionContent = courseContent.stream()
            .map(this::mapCourseSectionContent)
            .collect(Collectors.toList());

        sectionDto.setContent(sectionContent);
        return sectionDto;
    }

    private CourseSectionContentDto mapCourseSectionContent(CourseContent content) {
        CourseSectionContentDto courseSectionContentDto = new CourseSectionContentDto();
        GcVideo video = content.getVideo();

        courseSectionContentDto.setType(CourseContentType.VIDEO);
        courseSectionContentDto.setId(video.getId());
        courseSectionContentDto.setDuration(video.getVideoTime());
        courseSectionContentDto.setName(video.getVideoName());
        return courseSectionContentDto;
    }
}
