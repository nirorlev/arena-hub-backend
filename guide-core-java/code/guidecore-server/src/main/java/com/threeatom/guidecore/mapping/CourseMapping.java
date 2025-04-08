package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.dto.request.CourseSettingDto;
import com.threeatom.guidecore.dto.response.CourseDto;
import com.threeatom.guidecore.dto.response.CourseEnrollmentDto;
import com.threeatom.guidecore.dto.response.CourseProgramDto;
import com.threeatom.guidecore.dto.response.CourseProgramTaskDto;
import com.threeatom.guidecore.dto.response.CourseProgressDetailsDto;
import com.threeatom.guidecore.dto.response.CourseSectionContentDto;
import com.threeatom.guidecore.dto.response.CourseSectionDto;
import com.threeatom.guidecore.dto.response.CourseTotalProgressDto;
import com.threeatom.guidecore.dto.response.VideoSourceDto;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseEnrollmentProgress;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.enums.CourseContentType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

@Mapper(uses = {DateMapping.class, UserMapping.class})
public interface CourseMapping {

    @Mapping(target = "title", source = "name")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "thumbUrl", source = "subImgFile.fullFileUrl")
    CourseDto mapBasic(GcSubject course);

    @Named("mapCourseEnrollment")
    @Mapping(target = "title", source = "name")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "thumbUrl", source = "subImgFile.fullFileUrl")
    CourseEnrollmentDto mapCourseEnrollment(GcSubject course);

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "avatarUrl", source = "subImgFile.fullFileUrl")
    VideoSourceDto map(GcSubject course);

    @Mapping(target = "id", source = "course.id")
    CourseProgressDetailsDto mapToCourseProgress(GcSubject course, CourseSetting compliance);

    CourseSettingDto mapToSetting(CourseSetting courseSetting);

    @Mapping(target = "courseId", source = "courseId")
    @Mapping(target = "courseContentStudyPercentage", source = "courseSettingDto.courseContentStudyPercentage")
    @Mapping(target = "singleVideoViewPercentage", source = "courseSettingDto.singleVideoViewPercentage")
    CourseSetting mapToSetting(CourseSettingDto courseSettingDto, Integer courseId);

    CourseSetting mapToUpdateSetting(@MappingTarget CourseSetting courseSetting, CourseSettingDto courseSettingDto);

    @Mapping(target = "id", source = "course.id")
    @Mapping(target = "name", source = "course.name")
    @Mapping(target = "sections", source = "courseContent", qualifiedByName = "mapCourseSectionContent")
    CourseProgramDto mapProgram(GcSubject course, List<CourseContent> courseContent);

    CourseTotalProgressDto map(CourseEnrollmentProgress courseEnrollmentProgress, boolean isCompliant);

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
        courseSectionContentDto.setTasks(createCourseProgramTaskDtos(video.getTasks()));
        courseSectionContentDto.setName(video.getVideoName());
        return courseSectionContentDto;
    }

    private List<CourseProgramTaskDto> createCourseProgramTaskDtos(List<Task> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return List.of();
        }

        return tasks.stream()
            .map(task -> {
                CourseProgramTaskDto courseProgramTaskDto = new CourseProgramTaskDto();
                courseProgramTaskDto.setId(task.getId());
                courseProgramTaskDto.setType(task.getType());
                return courseProgramTaskDto;
            })
            .collect(Collectors.toList());
    }
}
