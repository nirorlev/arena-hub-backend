package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.CourseEnrollmentProgress;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.Task;
import com.threeatom.guidecore.entity.VideoEvent;
import com.threeatom.guidecore.enums.VideoEventType;
import com.threeatom.guidecore.mapper.CourseEnrollmentProgressMapper;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseEnrollmentProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.UserTaskAnswerService;
import com.threeatom.guidecore.service.VideoEventService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import com.threeatom.guidecore.util.TaskTimingUtil;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseEnrollmentProgressServiceImpl
    extends ServiceImpl<CourseEnrollmentProgressMapper, CourseEnrollmentProgress>
    implements CourseEnrollmentProgressService {

    private final GcSubjectService courseService;
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
        Optional<CourseEnrollment> courseEnrollment =
            courseEnrollmentService.findActiveCourseEnrollment(courseId, portalUser);

        if (courseEnrollment.isEmpty()) {
            log.error("User {} is not enrolled to course {}", portalUser.getUserId(), courseId);
        }

        List<GcVideo> videos = courseService.courseVideos(courseId);
        CourseSetting courseSetting = courseSettingService.findByCourseId(courseId);

        return courseEnrollment.map(
                enrollment -> {
                    CourseProgressDto courseProgressDto =
                        calculateProgressDto(course, videos, courseSetting, enrollment, portalUser);
                    updateEnrollment(courseProgressDto, enrollment, courseSetting);
                    return courseProgressDto;
                })
            .orElseGet(() -> emptyCourseProgressDto(course, courseSetting));
    }

    @Transactional
    void saveProgress(CourseEnrollmentProgress courseEnrollmentProgress) {
        save(courseEnrollmentProgress);
    }

    private CourseProgressDto calculateProgressDto(GcSubject course, List<GcVideo> videos,
                                                   CourseSetting courseSetting, CourseEnrollment enrollment,
                                                   PortalUser portalUser) {
        List<Integer> videoIds = courseVideoIds(videos);
        Map<Integer, List<Task>> videoIdToTasks = getVideoIdToTasks(videoIds);
        List<Task> courseTasks = getCourseTasks(videoIdToTasks);

        Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails =
            taskIdToProgress(courseTasks, portalUser);

        Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails =
            videoPlaySessionService.videoViewerDetails(videoIds, portalUser, enrollment.getStartDate(),
                OffsetDateTime.now());
        Map<Integer, ProgressDetailsDto<SectionProgressDto>> sectionsProgress =
            sectionsProgress(videos, videoIdToViewerVideoDetails, videoIdToTasks, taskIdToProgressDetails);

        CourseProgressDto courseProgressDto = new CourseProgressDto();
        CourseProgressDetailsDto courseProgressDetailsDto = courseMapping.mapToCourseProgress(course, courseSetting);
        CourseTotalProgressDto courseTotalProgressDto =
            courseProgress(videos, videoIdToViewerVideoDetails, sectionsProgress, courseTasks, taskIdToProgressDetails,
                enrollment.getComplianceDate() != null);
        courseProgressDetailsDto.setProgress(courseTotalProgressDto);

        courseProgressDto.setCourse(courseProgressDetailsDto);
        courseProgressDto.setSections(convertKeyToString(sectionsProgress));
        courseProgressDto.setContent(convertKeyToString(
            contentProgress(videos, videoIdToViewerVideoDetails, videoIdToTasks, taskIdToProgressDetails)));
        courseProgressDto.setTasks(convertKeyToString(taskIdToProgressDetails));

        return courseProgressDto;
    }

    private List<Task> getCourseTasks(Map<Integer, List<Task>> videoIdToTasks) {
        return videoIdToTasks.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private Map<Integer, List<Task>> getVideoIdToTasks(List<Integer> videoIds) {
        List<VideoEvent> taskVideoEvents = videoEventService.videoEventsByType(videoIds, VideoEventType.TASK);
        return taskVideoEvents.stream()
            .filter(videoEvent -> videoEvent.getTask() != null)
            .collect(Collectors.groupingBy(VideoEvent::getVideoId,
                Collectors.mapping(VideoEvent::getTask, Collectors.toList())));
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

    private Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgress(List<Task> tasks,
                                                                               PortalUser portalUser) {
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
                                                  Map<Integer, ProgressDetailsDto<TaskProgressDto>> taskIdToProgressDetails,
                                                  boolean isCompliant) {
        CourseTotalProgressDto courseTotalProgressDto = new CourseTotalProgressDto();

        int secondsViewed = videoIdToVideoViewerDetails.values().stream()
            .mapToInt(VideoViewerVideoDetailDto::getUniqueViewTime)
            .sum();
        int sectionsCompleted = (int) sectionsProgress.values().stream()
            .filter(progressDetailsDto -> progressDetailsDto.getProgress().getPercentage() >= 99)
            .count();
        List<Task> correctlyAnsweredTasks = correctlyAnsweredTasks(tasks, taskIdToProgressDetails);
        int correctTaskTime = tasksTime(correctlyAnsweredTasks);
        int totalTaskTime = tasksTime(tasks);

        courseTotalProgressDto.setSecondsViewed(secondsViewed);
        courseTotalProgressDto.setCompletedSectionsCount(sectionsCompleted);
        courseTotalProgressDto.setCompletedTasksCount(correctlyAnsweredTasks.size());
        courseTotalProgressDto.setCompliant(isCompliant);
        courseTotalProgressDto.setPercentage(
            progressPercent(getTotalVideoTime(videos), secondsViewed, totalTaskTime, correctTaskTime));

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
            progressPercent(totalSectionVideoTime, sectionVideosSecondsViewed, totalTaskTime,
                tasksTime(correctlyAnsweredTasks)));
        progressDto.setSecondsViewed(sectionVideosSecondsViewed);
        progressDto.setCompletedTasksCount(correctlyAnsweredTasks.size());
        progressDetailsDto.setProgress(progressDto);

        return progressDetailsDto;
    }

    private double progressPercent(int totalTime, int viewedTime, int totalTaskTime, int correctTasksTime) {
        return (viewedTime + correctTasksTime) / ((double) (totalTime + totalTaskTime)) * 100;
    }

    private int secondsViewed(List<GcVideo> videos, Map<Integer, Integer> videoIdToSecondsWatched) {
        return videos.stream()
            .mapToInt(video -> videoIdToSecondsWatched.getOrDefault(video.getId(), 0))
            .sum();
    }

    private void updateEnrollment(CourseProgressDto courseProgressDto, CourseEnrollment courseEnrollment,
                                  CourseSetting courseSetting) {
        CourseEnrollmentProgress courseEnrollmentProgress =
            createCourseProgress(courseEnrollment, courseProgressDto.getCourse().getProgress());
        if (wasProgressUpdated(courseEnrollment, courseEnrollmentProgress)) {
            saveProgress(courseEnrollmentProgress);
            courseEnrollment.setUpdatedTime(OffsetDateTime.now());
        }

        int tasksCount = courseProgressDto.getTasks().size();
        double taskCompletionPercentage = taskPercentage(tasksCount, courseEnrollmentProgress.getCompletedTasksCount());
        double courseCompletionPercentage = courseEnrollmentProgress.getPercentage();
        boolean taskProgressCompliant = isCompliant(courseSetting.getTasksGradePercentage(), taskCompletionPercentage);
        boolean courseContentProgressCompliant =
            isCompliant(courseSetting.getCourseContentStudyPercentage(), courseCompletionPercentage);

        updateEnrollmentCompliance(courseEnrollment, taskProgressCompliant, courseContentProgressCompliant);
        updateEnrollmentCompletion(courseEnrollment, courseCompletionPercentage);

        courseEnrollmentService.updateById(courseEnrollment);
    }

    private CourseEnrollmentProgress createCourseProgress(CourseEnrollment courseEnrollment,
                                                          CourseTotalProgressDto courseTotalProgressDto) {
        CourseEnrollmentProgress courseEnrollmentProgress = new CourseEnrollmentProgress();
        courseEnrollmentProgress.setEnrollmentId(courseEnrollment.getId());
        courseEnrollmentProgress.setPercentage(courseTotalProgressDto.getPercentage());
        courseEnrollmentProgress.setCompletedSectionsCount(courseTotalProgressDto.getCompletedSectionsCount());
        courseEnrollmentProgress.setCompletedTasksCount(courseTotalProgressDto.getCompletedTasksCount());
        courseEnrollmentProgress.setSecondsViewed(courseTotalProgressDto.getSecondsViewed());

        return courseEnrollmentProgress;
    }

    private void updateEnrollmentCompletion(CourseEnrollment courseEnrollment, double courseCompletionPercentage) {
        if (courseEnrollment.getCompletionDate() == null && courseCompletionPercentage >= 99) {
            courseEnrollment.setCompletionDate(OffsetDateTime.now());
            courseEnrollment.setUpdatedTime(OffsetDateTime.now());
        }
    }

    private void updateEnrollmentCompliance(CourseEnrollment courseEnrollment, boolean taskProgressCompliant,
                                            boolean courseContentProgressCompliant) {
        if (courseEnrollment.getComplianceDate() == null && taskProgressCompliant && courseContentProgressCompliant) {
            updateEnrollmentCompliance(courseEnrollment, OffsetDateTime.now());
        }
    }

    private boolean wasProgressUpdated(CourseEnrollment courseEnrollment,
                                       CourseEnrollmentProgress courseEnrollmentProgress) {
        return courseEnrollment.getLatestProgress().isEmpty() ||
            !courseEnrollment.getLatestProgress().get().equals(courseEnrollmentProgress);
    }

    private void updateEnrollmentCompliance(CourseEnrollment courseEnrollment, OffsetDateTime complianceDate) {
        courseEnrollment.setComplianceDate(complianceDate);
        courseEnrollment.setUpdatedTime(OffsetDateTime.now());
    }

    private boolean isCompliant(double thresholdPercent, double currentPercentage) {
        return thresholdPercent >= currentPercentage;
    }

    private double taskPercentage(int tasksCount, Integer completedTasksCount) {
        return (double) completedTasksCount / tasksCount * 100;
    }

    private <T> Map<String, T> convertKeyToString(Map<Integer, T> sections) {
        return sections.entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
    }

    private CourseProgressDto emptyCourseProgressDto(GcSubject course, CourseSetting courseSetting) {
        CourseProgressDto courseProgressDto = new CourseProgressDto();
        courseProgressDto.setCourse(courseMapping.mapToCourseProgress(course, courseSetting));
        return courseProgressDto;
    }

}
