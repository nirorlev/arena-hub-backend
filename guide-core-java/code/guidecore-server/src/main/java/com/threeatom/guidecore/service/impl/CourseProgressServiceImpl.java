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

        List<GcVideo> videos = courseContentService.findCourseContent(courseId).stream()
            .map(CourseContent::getVideo)
            .filter(video -> video.getSubId() != null)
            .collect(Collectors.toList());
        List<Integer> videoIds = videos.stream()
            .map(GcVideo::getId)
            .collect(Collectors.toList());

        OffsetDateTime start = courseEnrollment != null ? courseEnrollment.getCreateTime() : OffsetDateTime.MIN;
        Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails =
            videoPlaySessionService.videoViewerDetails(videoIds, portalUser, start,
                courseProgressEndDate(courseEnrollment));
        CourseSetting courseSetting = courseSettingService.findByCourseId(courseId);


        CourseProgressDto courseProgressDto = new CourseProgressDto();
        CourseProgressDetailsDto courseProgressDetailsDto = courseMapping.mapToCourseProgress(course, courseSetting);
        Map<Integer, ProgressDetailsDto> sections = sectionsProgress(videos, videoIdToViewerVideoDetails);
        courseProgressDetailsDto.setProgress(getCourseProgress(sections));

        courseProgressDto.setCourse(courseProgressDetailsDto);
        courseProgressDto.setSections(sections);
        courseProgressDto.setContent(contentProgress(videos, videoIdToViewerVideoDetails));
        return courseProgressDto;
    }

    private Map<Integer, ProgressDetailsDto> contentProgress(List<GcVideo> videos,
                                                             Map<Integer, VideoViewerVideoDetailDto> videoIdToViewerVideoDetails) {
        return videos.stream()
            .collect(Collectors.toMap(GcVideo::getId, video -> {
                ProgressDetailsDto progressDetailsDto = new ProgressDetailsDto();
                ProgressDto progressDto = new ProgressDto();
                VideoViewerVideoDetailDto videoViewerVideoDetailDto = videoIdToViewerVideoDetails.get(video.getId());
                progressDetailsDto.setProgress(progressDto);
                if (videoViewerVideoDetailDto == null) {
                    return progressDetailsDto;
                }

                progressDto.setPercentage(videoViewerVideoDetailDto.getPercentageViewed());
                progressDto.setSecondsViewed(videoViewerVideoDetailDto.getTotalViewTime());
                return progressDetailsDto;
            }));
    }

    private CourseTotalProgressDto getCourseProgress(Map<Integer, ProgressDetailsDto> sections) {
        CourseTotalProgressDto courseTotalProgressDto = new CourseTotalProgressDto();
        int secondsViewed = sections.values().stream()
            .map(ProgressDetailsDto::getProgress)
            .mapToInt(ProgressDto::getSecondsViewed)
            .sum();
        int sectionsCompleted = (int) sections.values().stream()
            .filter(progressDetailsDto -> progressDetailsDto.getProgress().getPercentage() > 90)
            .count();

        courseTotalProgressDto.setSecondsViewed(secondsViewed);
        courseTotalProgressDto.setCompletedSectionsCount(sectionsCompleted);
        courseTotalProgressDto.setPercentage((double) sectionsCompleted / sections.size() * 100);

        return courseTotalProgressDto;
    }

    private Map<Integer, ProgressDetailsDto> sectionsProgress(List<GcVideo> videos,
                                                              Map<Integer, VideoViewerVideoDetailDto> videoIdToVideoViewerDetails) {
        Map<Integer, List<GcVideo>> courseSectionIdToVideos = videos.stream()
            .collect(Collectors.groupingBy(GcVideo::getSubId));
        Map<Integer, Double> videoIdToProgress = videoIdToVideoViewerDetails.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPercentageViewed()));
        Map<Integer, Integer> videoIdToSecondsWatched = videoIdToVideoViewerDetails.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getTotalViewTime()));

        return courseSectionIdToVideos.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<GcVideo> sectionVideos = entry.getValue();
                int sectionVideosSize = sectionVideos.size();
                long sectionViewedVideos = videos.stream()
                    .filter(video -> videoIdToProgress.getOrDefault(video.getId(), 0d) > 90)
                    .count();
                double percentage = sectionViewedVideos / (double) sectionVideosSize;
                int secondsWatched = videos.stream()
                    .mapToInt(video -> videoIdToSecondsWatched.getOrDefault(video.getId(), 0))
                    .sum();

                ProgressDetailsDto progressDetailsDto = new ProgressDetailsDto();
                ProgressDto progressDto = new ProgressDto();

                progressDto.setPercentage(percentage);
                progressDto.setSecondsViewed(secondsWatched);

                progressDetailsDto.setProgress(progressDto);
                return progressDetailsDto;
            }));
    }

    private OffsetDateTime courseProgressEndDate(CourseEnrollment courseEnrollment) {
        if (courseEnrollment == null) {
            return OffsetDateTime.MAX;
        }
        return courseEnrollment.getCompletionDate() != null
            ? courseEnrollment.getCompletionDate()
            : OffsetDateTime.MAX;
    }
}
