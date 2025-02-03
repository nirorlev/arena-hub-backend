package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.response.CourseProgressDto;
import com.threeatom.guidecore.dto.response.ProgressDetailsDto;
import com.threeatom.guidecore.entity.CourseContent;
import com.threeatom.guidecore.entity.CourseEnrollment;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapping.CourseMapping;
import com.threeatom.guidecore.service.CourseContentService;
import com.threeatom.guidecore.service.CourseEnrollmentService;
import com.threeatom.guidecore.service.CourseProgressService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.VideoPlaySegmentService;
import java.util.Collection;
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
    private final VideoPlaySegmentService videoPlaySegmentService;
    private final CourseEnrollmentService courseEnrollmentService;

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

        Map<Integer, Double> videoIdToProgress = videoPlaySegmentService.getVideoProgress(videos, portalUser, courseEnrollment);
        Map<Integer, Double> sectionIdToProgress = sectionsProgress(videos, videoIdToProgress);

        CourseProgressDto courseProgressDto = new CourseProgressDto();
        courseProgressDto.setCourse(courseMapping.mapToCourseProgress(course, getCourseProgress(
            sectionIdToProgress.values())));
        courseProgressDto.setContent(convertToProgressDto(videoIdToProgress));
        courseProgressDto.setSections(convertToProgressDto(sectionIdToProgress));
        return courseProgressDto;
    }

    private double getCourseProgress(Collection<Double> sectionsProgress) {
        return sectionsProgress.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0);
    }

    private Map<Integer, Double> sectionsProgress(List<GcVideo> videos, Map<Integer, Double> videoIdToProgress) {
        Map<Integer, List<GcVideo>> courseSectionIdToVideos = videos.stream()
            .collect(Collectors.groupingBy(GcVideo::getSubId));

        return courseSectionIdToVideos.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<GcVideo> sectionVideos = entry.getValue();
                int sectionVideosSize = sectionVideos.size();
                long sectionViewedVideos = videos.stream()
                    .filter(video -> videoIdToProgress.getOrDefault(video.getId(), 0d) > 90)
                    .count();

                return sectionViewedVideos / (double) sectionVideosSize;
            }));
    }

    private Map<Integer, ProgressDetailsDto> convertToProgressDto(Map<Integer, Double> videoIdToProgress) {
        return videoIdToProgress.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                ProgressDetailsDto progressDetailsDto = new ProgressDetailsDto();
                progressDetailsDto.setProgress(courseMapping.mapToProgress(entry.getValue()));
                return progressDetailsDto;
            }));
    }
}
