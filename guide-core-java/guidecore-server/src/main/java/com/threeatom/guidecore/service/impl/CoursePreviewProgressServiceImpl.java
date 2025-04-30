package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.ContentProgressDto;
import com.threeatom.guidecore.dto.response.CourseProgressDetailsDto;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.CourseTotalProgressDto;
import com.threeatom.guidecore.dto.response.ProgressDetailsDto;
import com.threeatom.guidecore.dto.response.SectionProgressDto;
import com.threeatom.guidecore.dto.response.TaskProgressDto;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CoursePreviewProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.VideoEventService;
import com.threeatom.guidecore.util.CollectionUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoursePreviewProgressServiceImpl implements CoursePreviewProgressService {

    private final GcSubjectService courseService;
    private final AuthorizationService authorizationService;
    private final CourseMapping courseMapping;
    private final CourseSettingService courseSettingService;
    private final VideoEventService videoEventService;

    @Override
    public CourseProgressDto courseProgress(Integer courseId, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            log.error("User {} has no access to course {}", portalUser.getUserId(), courseId);
            throw new ForbiddenException("You have no access to this course");
        }
        List<GcVideo> videos = courseService.courseVideos(courseId);
        CourseSetting courseSetting = courseSettingService.findByCourseId(courseId).orElseGet(CourseSetting::new);

        return calculatePreviewProgressDto(course, videos, courseSetting);
    }

    private CourseProgressDto calculatePreviewProgressDto(GcSubject course, List<GcVideo> videos,
                                                          CourseSetting courseSetting) {
        List<Integer> videoIds = courseVideoIds(videos);
        Map<Integer, List<Task>> videoIdToTasks = getVideoIdToTasks(videoIds);
        List<Task> courseTasks = getCourseTasks(videoIdToTasks);

        return getCourseProgressDto(
            getPreviewTaskProgress(courseTasks)
            , getPreviewContentProgress(videos)
            , getPreviewSectionProgress(videos)
            , getPreviewCourseProgress(course, videos, courseSetting)
        );
    }

    private Map<String, ProgressDetailsDto<TaskProgressDto>> getPreviewTaskProgress(List<Task> courseTasks) {
        return courseTasks.stream()
            .collect(Collectors.toMap(task -> String.valueOf(task.getId()), task -> {
                ProgressDetailsDto<TaskProgressDto> taskProgress = new ProgressDetailsDto<>();
                taskProgress.setProgress(new TaskProgressDto());
                return taskProgress;
            }));
    }

    private Map<String, ProgressDetailsDto<ContentProgressDto>> getPreviewContentProgress(List<GcVideo> videos) {
        return videos.stream()
            .collect(Collectors.toMap(video -> String.valueOf(video.getId()), video -> {
                ProgressDetailsDto<ContentProgressDto> videoProgress = new ProgressDetailsDto<>();
                double percentage = getRandomPercentage();
                ContentProgressDto contentProgressDto = new ContentProgressDto();
                contentProgressDto.setPercentage(percentage);
                contentProgressDto.setSecondsViewed(getSecondsViewed(video.getVideoTime(), percentage));
                videoProgress.setProgress(contentProgressDto);
                return videoProgress;
            }));
    }

    private int getSecondsViewed(Integer videoTime, double percentage) {
        return (int) (videoTime * percentage / 100);
    }

    private double getRandomPercentage() {
        return Math.min(1.0, Math.random() + Double.MIN_VALUE);
    }

    private Map<String, ProgressDetailsDto<SectionProgressDto>> getPreviewSectionProgress(List<GcVideo> videos) {
        Map<Integer, List<GcVideo>> courseSectionIdToVideos = videos.stream()
            .collect(Collectors.groupingBy(GcVideo::getSubId));
        return courseSectionIdToVideos.entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), entry -> {
                ProgressDetailsDto<SectionProgressDto> sectionProgressDetails = new ProgressDetailsDto<>();
                SectionProgressDto sectionProgressDto = new SectionProgressDto();
                double percentage = getRandomPercentage();
                sectionProgressDto.setPercentage(percentage);
                sectionProgressDto.setSecondsViewed(getSecondsViewed(totalVideoTime(entry.getValue()), percentage));
                sectionProgressDetails.setProgress(sectionProgressDto);
                return sectionProgressDetails;
            }));
    }

    private int totalVideoTime(List<GcVideo> videos) {
        return videos.stream().mapToInt(GcVideo::getVideoTime).sum();
    }

    private CourseProgressDetailsDto getPreviewCourseProgress(GcSubject course, List<GcVideo> videos,
                                                              CourseSetting courseSetting) {
        CourseProgressDetailsDto courseProgressDetailsDto = courseMapping.mapToCourseProgress(course, courseSetting);
        CourseTotalProgressDto courseTotalProgressDto = new CourseTotalProgressDto();
        double percentage = getRandomPercentage();
        courseTotalProgressDto.setPercentage(percentage);
        courseTotalProgressDto.setSecondsViewed(getSecondsViewed(totalVideoTime(videos), percentage));
        courseProgressDetailsDto.setProgress(courseTotalProgressDto);
        return courseProgressDetailsDto;
    }

    private CourseProgressDto getCourseProgressDto(
        Map<String, ProgressDetailsDto<TaskProgressDto>> tasksProgress,
        Map<String, ProgressDetailsDto<ContentProgressDto>> contentProgress,
        Map<String, ProgressDetailsDto<SectionProgressDto>> sectionsProgress,
        CourseProgressDetailsDto courseProgressDetailsDto) {

        CourseProgressDto courseProgressDto = new CourseProgressDto();
        courseProgressDto.setTasks(tasksProgress);
        courseProgressDto.setContent(contentProgress);
        courseProgressDto.setSections(sectionsProgress);
        courseProgressDto.setCourse(courseProgressDetailsDto);
        return courseProgressDto;
    }

    private List<Task> getCourseTasks(Map<Integer, List<Task>> videoIdToTasks) {
        return CollectionUtils.flattenValues(videoIdToTasks);
    }

    private Map<Integer, List<Task>> getVideoIdToTasks(List<Integer> videoIds) {
        List<VideoEvent> taskVideoEvents = videoEventService.videoEventsByType(videoIds, VideoEventType.TASK);
        return CollectionUtils.groupByAndMap(
            taskVideoEvents, VideoEvent::getVideoId, VideoEvent::getTask, event -> event.getTask() != null);
    }

    private List<Integer> courseVideoIds(List<GcVideo> videos) {
        return CollectionUtils.mapToList(videos, GcVideo::getId);
    }
}
