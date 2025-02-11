package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.CourseProgressDetailsDto;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.CourseTotalProgressDto;
import com.threeatom.guidecore.dto.response.ProgressDetailsDto;
import com.threeatom.guidecore.dto.response.ProgressDto;
import com.threeatom.guidecore.dto.response.analytic.VideoViewerVideoDetailDto;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.CourseSetting;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseContentService;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseProgressService;
import com.threeatom.guidecore.service.CourseSettingService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.VideoPlaySessionService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseProgressServiceImpl implements CourseProgressService {
    private final GcSubjectService courseService;
    private final CourseContentService courseContentService;
    private final AuthorizationService authorizationService;
    private final CourseMapping courseMapping;
    private final VideoPlaySessionService videoPlaySessionService;
    private final CourseEnrollmentService courseEnrollmentService;
    private final CourseSettingService courseSettingService;

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
        Integer videoViewPercentage =
            Optional.ofNullable(courseSetting).map(CourseSetting::getSingleVideoViewPercentage).orElse(90);

        Map<Integer, ProgressDetailsDto> sectionsProgress =
            sectionsProgress(videos, videoIdToViewerVideoDetails, videoViewPercentage);

        CourseProgressDto courseProgressDto = new CourseProgressDto();
        CourseProgressDetailsDto courseProgressDetailsDto = courseMapping.mapToCourseProgress(course, courseSetting);
        courseProgressDetailsDto.setProgress(
            courseProgress(videos, videoIdToViewerVideoDetails, sectionsProgress, videoViewPercentage));

        courseProgressDto.setCourse(courseProgressDetailsDto);
        courseProgressDto.setSections(convertKeyToString(sectionsProgress));
        courseProgressDto.setContent(convertKeyToString(contentProgress(videos, videoIdToViewerVideoDetails)));
        return courseProgressDto;
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

    private Map<Integer, ProgressDetailsDto> contentProgress(List<GcVideo> videos,
                                                             Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails) {
        return videos.stream()
            .collect(
                Collectors.toMap(GcVideo::getId, video -> videoContentProgress(videoIdToViewerVideoDetails, video)));
    }

    private ProgressDetailsDto videoContentProgress(
        Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails, GcVideo video) {

        VideoViewerVideoDetailDto videoViewerVideoDetailDto = videoIdToViewerVideoDetails.get(video.getId());
        ProgressDetailsDto progressDetailsDto = new ProgressDetailsDto();
        if (videoViewerVideoDetailDto == null) {
            return progressDetailsDto;
        }

        progressDetailsDto.setProgress(videoContentProgress(videoViewerVideoDetailDto));
        return progressDetailsDto;
    }

    private ProgressDto videoContentProgress(VideoViewerVideoDetailDto videoViewerVideoDetailDto) {
        ProgressDto progressDto = new ProgressDto();

        progressDto.setPercentage(videoViewerVideoDetailDto.getPercentageViewed());
        progressDto.setSecondsViewed(videoViewerVideoDetailDto.getTotalViewTime());

        return progressDto;
    }

    private CourseTotalProgressDto courseProgress(List<GcVideo> videos,
                                                  Map<Integer, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails,
                                                  Map<Integer, ProgressDetailsDto> sectionsProgress,
                                                  Integer videoViewPercentage) {
        CourseTotalProgressDto courseTotalProgressDto = new CourseTotalProgressDto();
        if (videoIdToVideoViewerDetails.isEmpty()) {
            return courseTotalProgressDto;
        }

        int secondsViewed = videoIdToVideoViewerDetails.values().stream()
            .mapToInt(VideoViewerVideoDetailDto::getTotalViewTime)
            .sum();
        int sectionsCompleted = (int) sectionsProgress.values().stream()
            .filter(progressDetailsDto -> progressDetailsDto.getProgress().getPercentage() == 100)
            .count();
        long videoContentCompleted = videoIdToVideoViewerDetails.values().stream()
            .filter(videoViewerVideoDetailDto -> videoViewerVideoDetailDto.getPercentageViewed() > videoViewPercentage)
            .count();

        courseTotalProgressDto.setSecondsViewed(secondsViewed);
        courseTotalProgressDto.setCompletedSectionsCount(sectionsCompleted);
        courseTotalProgressDto.setPercentage((double) videoContentCompleted / videos.size() * 100);

        return courseTotalProgressDto;
    }

    private Map<Integer, ProgressDetailsDto> sectionsProgress(List<GcVideo> videos,
                                                              Map<Integer, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails,
                                                              Integer videoViewPercentage) {
        Map<Integer, List<GcVideo>> courseSectionIdToVideos = videos.stream()
            .collect(Collectors.groupingBy(GcVideo::getSubId));
        Map<Integer, Double> videoIdToPercentViewed = videoIdToVideoViewerDetails.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPercentageViewed()));
        Map<Integer, Integer> videoIdToSecondsWatched = videoIdToVideoViewerDetails.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getTotalViewTime()));

        return courseSectionIdToVideos.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                long sectionViewedVideos = sectionViewedVideos(videos, videoIdToPercentViewed, videoViewPercentage);
                int secondsWatched = secondsWatched(videos, videoIdToSecondsWatched);

                return sectionProgressDetails(entry.getValue(), sectionViewedVideos, secondsWatched);
            }));
    }

    private ProgressDetailsDto sectionProgressDetails(List<GcVideo> sectionVideos, long sectionViewedVideosCount,
                                                      int sectionVideosSecondsViewed) {
        ProgressDetailsDto progressDetailsDto = new ProgressDetailsDto();
        ProgressDto progressDto = new ProgressDto();

        progressDto.setPercentage(sectionViewedVideosCount / (double) sectionVideos.size() * 100);
        progressDto.setSecondsViewed(sectionVideosSecondsViewed);
        progressDetailsDto.setProgress(progressDto);

        return progressDetailsDto;
    }

    private long sectionViewedVideos(List<GcVideo> videos, Map<Integer, Double> videoIdToProgress,
                                     Integer videoViewPercentage) {
        return videos.stream()
            .filter(video -> videoIdToProgress.getOrDefault(video.getId(), 0d) > videoViewPercentage)
            .count();
    }

    private int secondsWatched(List<GcVideo> videos, Map<Integer, Integer> videoIdToSecondsWatched) {
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
        return sections.entrySet().stream()
            .collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
    }
}
