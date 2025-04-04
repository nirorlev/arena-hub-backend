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
import com.threeatom.guidecore.dto.response.analytic.VideoViewerVideoDetailDto;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseContentService;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoEventService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import com.threeatom.guidecore.util.TaskTimingUtil;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {

    private final GcSubjectService courseService;
    private final CourseContentService courseContentService;
    private final AuthorizationService authorizationService;
    private final CourseMapping courseMapping;
    private final VideoPlaySessionService videoPlaySessionService;
    private final CourseEnrollmentService courseEnrollmentService;
    private final CourseSettingService courseSettingService;
    private final VideoEventService videoEventService;
    private final UserTaskAnswerService userTaskAnswerService;

    @Override
    public CourseProgressDto courseProgress(Integer courseId, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);
        if (!authorizationService.checkAccess(course, PermitAction.VIEW, portalUser)) {
            log.error("User {} has no access to course {}", portalUser.getUserId(), courseId);
            throw new ForbiddenException("You have no access to this course");
        }
        CourseEnrollment courseEnrollment = courseEnrollmentService.getCourseEnrollment(portalUser, courseId);
        if (courseEnrollment == null) {
            log.error("User {} is not enrolled to course {}", portalUser.getUserId(), courseId);
        }

        List<GcVideo> videos = courseVideos(courseId);
        List<Integer> videoIds = courseVideoIds(videos);

        Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails =
            videoIdToViewerVideoDetails(portalUser, courseEnrollment, videoIds);
        CourseSetting courseSetting = courseSettingService.findByCourseId(courseId);
        List<VideoEvent> taskVideoEvents =
            videoEventService.videoEventsByType(videoIds, VideoEventType.TASK);
        Map<Integer, List<Task>> videoIdToTasks = taskVideoEvents.stream()
            .filter(videoEvent -> videoEvent.getTask() != null)
            .collect(Collectors.groupingBy(VideoEvent::getVideoId,
                Collectors.mapping(VideoEvent::getTask, Collectors.toList())));
        List<Task> allTasks = videoIdToTasks.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails = courseTasks(allTasks, portalUser);
        Map<Integer, ProgressDetailsDto<SectionProgressDto>> sectionsProgress =
            sectionsProgress(videos, videoIdToViewerVideoDetails, videoIdToTasks, taskIdToProgressDetails);

        CourseProgressDto courseProgressDto = new CourseProgressDto();
        CourseProgressDetailsDto courseProgressDetailsDto = courseMapping.mapToCourseProgress(course, courseSetting);
        CourseTotalProgressDto courseTotalProgressDto =
            courseProgress(videos, videoIdToViewerVideoDetails, sectionsProgress, allTasks, taskIdToProgressDetails);
        courseProgressDetailsDto.setProgress(courseTotalProgressDto);

        courseProgressDto.setCourse(courseProgressDetailsDto);
        courseProgressDto.setSections(convertKeyToString(sectionsProgress));
        courseProgressDto.setContent(convertKeyToString(
            contentProgress(videos, videoIdToViewerVideoDetails, videoIdToTasks, taskIdToProgressDetails)));
        courseProgressDto.setTasks(convertKeyToString(taskIdToProgressDetails));

        return courseProgressDto;
    }

    private int tasksTime(List<Task> tasks) {
        return tasks.stream()
            .mapToInt(task -> TaskTimingUtil.getTaskTiming(task.getType()))
            .sum();
    }

    private List<Task> correctlyAnsweredTasks(List<Task> tasks,
                                              Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {
        return tasks.stream()
            .filter(task -> taskIdToProgressDetails.containsKey(task.getId()))
            .filter(task -> taskIdToProgressDetails.get(task.getId()).getProgress().isCompleted())
            .collect(Collectors.toList());
    }

    private Map<Integer, ProgressDetailsDto<TaskProgressDto>> courseTasks(List<Task> tasks, PortalUser portalUser) {
        List<Integer> taskIds = tasks.stream()
            .map(Task::getId)
            .collect(Collectors.toList());

        return userTaskAnswerService.taskIdToProgress(taskIds, portalUser);
    }

    private List<Integer> courseVideoIds(List<GcVideo> videos) {
        return videos.stream()
            .map(GcVideo::getId)
            .collect(Collectors.toList());
    }

    private List<GcVideo> courseVideos(Integer courseId) {
        return courseContentService.findCourseContent(courseId).stream()
            .map(CourseContent::getVideo)
            .filter(video -> video.getSubId() != null)
            .collect(Collectors.toList());
    }

    private Map<Integer, ProgressDetailsDto<ContentProgressDto>> contentProgress(List<GcVideo> videos,
                                                                                 Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails,
                                                                                 Map<Integer, List<Task>> videoIdToTasks,
                                                                                 Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {
        return videos.stream()
            .collect(
                Collectors.toMap(GcVideo::getId, video -> {
                    List<Task> videoTasks = videoIdToTasks.getOrDefault(video.getId(), List.of());
                    return videoContentProgress(videoIdToViewerVideoDetails, video, videoTasks,
                        taskIdToProgressDetails);
                }));
    }

    private ProgressDetailsDto<ContentProgressDto> videoContentProgress(
        Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails, GcVideo video, List<Task> videoTasks,
        Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {

        VideoViewerVideoDetailDto videoViewerVideoDetailDto = videoIdToViewerVideoDetails.get(video.getId());

        ProgressDetailsDto<ContentProgressDto> progressDetailsDto = new ProgressDetailsDto<>();
        progressDetailsDto.setProgress(
            videoContentProgress(videoViewerVideoDetailDto, videoTasks, taskIdToProgressDetails));
        return progressDetailsDto;
    }

    private ContentProgressDto videoContentProgress(VideoViewerVideoDetailDto videoViewerVideoDetailDto,
                                                    List<Task> videoTasks,
                                                    Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {
        ContentProgressDto progressDto = new ContentProgressDto();

        if (videoViewerVideoDetailDto != null) {
            progressDto.setPercentage(videoViewerVideoDetailDto.getPercentageViewed());
            progressDto.setSecondsViewed(videoViewerVideoDetailDto.getUniqueViewTime());
        }
        progressDto.setCompletedTasksCount(correctlyAnsweredTasks(videoTasks, taskIdToProgressDetails).size());

        return progressDto;
    }

    private CourseTotalProgressDto courseProgress(List<GcVideo> videos,
                                                  Map<Integer, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails,
                                                  Map<Integer, ProgressDetailsDto<SectionProgressDto>> sectionsProgress,
                                                  List<Task> tasks,
                                                  Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {
        CourseTotalProgressDto courseTotalProgressDto = new CourseTotalProgressDto();

        int secondsViewed = videoIdToVideoViewerDetails.values().stream()
            .mapToInt(VideoViewerVideoDetailDto::getUniqueViewTime)
            .sum();
        int sectionsCompleted = (int) sectionsProgress.values().stream()
            .filter(progressDetailsDto -> progressDetailsDto.getProgress().getPercentage() == 100)
            .count();
        List<Task> correctlyAnsweredTasks = correctlyAnsweredTasks(tasks, taskIdToProgressDetails);
        int correctTaskTime = tasksTime(correctlyAnsweredTasks);
        int totalTaskTime = tasksTime(tasks);

        courseTotalProgressDto.setSecondsViewed(secondsViewed);
        courseTotalProgressDto.setCompletedSectionsCount(sectionsCompleted);
        courseTotalProgressDto.setCompletedTasksCount(correctlyAnsweredTasks.size());
        courseTotalProgressDto.setPercentage(
            ((double) (secondsViewed + correctTaskTime)) / (getTotalVideoTime(videos) + totalTaskTime) * 100);

        return courseTotalProgressDto;
    }

    private Map<Integer, ProgressDetailsDto<SectionProgressDto>> sectionsProgress(List<GcVideo> videos,
                                                                                  Map<Integer, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails,
                                                                                  Map<Integer, List<Task>> videoIdToTasks,
                                                                                  Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails) {
        Map<Integer, List<GcVideo>> courseSectionIdToVideos = videos.stream()
            .collect(Collectors.groupingBy(GcVideo::getSubId));
        Map<Integer, Integer> videoIdToUniqueSecondsViewed = videoIdToVideoViewerDetails.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getUniqueViewTime()));

        return courseSectionIdToVideos.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<GcVideo> sectionVideos = entry.getValue();
                int secondsViewed = secondsViewed(sectionVideos, videoIdToUniqueSecondsViewed);
                List<Task> sectionTasks = videoTasks(sectionVideos, videoIdToTasks);
                List<Task> correctlyAnsweredTasks = correctlyAnsweredTasks(sectionTasks, taskIdToProgressDetails);
                int totalTaskTime = tasksTime(sectionTasks);

                return sectionProgressDetails(secondsViewed, getTotalVideoTime(sectionVideos), correctlyAnsweredTasks,
                    totalTaskTime);
            }));
    }

    private List<Task> videoTasks(List<GcVideo> sectionVideos, Map<Integer, List<Task>> videoIdToTasks) {
        return sectionVideos.stream()
            .map(GcVideo::getId)
            .filter(videoIdToTasks::containsKey)
            .map(videoIdToTasks::get)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private int getTotalVideoTime(List<GcVideo> videos) {
        return videos.stream()
            .mapToInt(GcVideo::getVideoTime)
            .sum();
    }

    private ProgressDetailsDto<SectionProgressDto> sectionProgressDetails(int sectionVideosSecondsViewed,
                                                                          int totalSectionVideoTime,
                                                                          List<Task> correctlyAnsweredTasks,
                                                                          int totalTaskTime) {
        ProgressDetailsDto<SectionProgressDto> progressDetailsDto = new ProgressDetailsDto<>();
        SectionProgressDto progressDto = new SectionProgressDto();

        progressDto.setPercentage(
            (sectionVideosSecondsViewed + tasksTime(correctlyAnsweredTasks)) /
                ((double) (totalSectionVideoTime + totalTaskTime)) * 100);
        progressDto.setSecondsViewed(sectionVideosSecondsViewed);
        progressDto.setCompletedTasksCount(correctlyAnsweredTasks.size());
        progressDetailsDto.setProgress(progressDto);

        return progressDetailsDto;
    }

    private int secondsViewed(List<GcVideo> videos, Map<Integer, Integer> videoIdToSecondsWatched) {
        return videos.stream()
            .mapToInt(video -> videoIdToSecondsWatched.getOrDefault(video.getId(), 0))
            .sum();
    }

    private Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails(PortalUser portalUser,
                                                                                CourseEnrollment courseEnrollment,
                                                                                List<Integer> videoIds) {
        OffsetDateTime start = courseEnrollment != null ? courseEnrollment.getCreateTime() : OffsetDateTime.MIN;
        OffsetDateTime end = courseProgressEndDate(courseEnrollment);

        return videoPlaySessionService.videoViewerDetails(videoIds, portalUser, start, end);
    }

    private OffsetDateTime courseProgressEndDate(CourseEnrollment courseEnrollment) {
        if (courseEnrollment == null) {
            return OffsetDateTime.MAX;
        }
        return courseEnrollment.getCompletionDate() != null
            ? courseEnrollment.getCompletionDate()
            : OffsetDateTime.MAX;
    }

    private <T> Map<String, T> convertKeyToString(Map<Integer, T> sections) {
        if (sections.isEmpty()) {
            return null;
        }

        return sections.entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
    }
}
