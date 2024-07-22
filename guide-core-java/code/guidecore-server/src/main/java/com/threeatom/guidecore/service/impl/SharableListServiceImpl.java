package com.threeatom.guidecore.service.impl;

import com.threeatom.common.permit.service.PermitService;
import com.threeatom.guidecore.dto.response.AccessGroupDetailsDto;
import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.mapping.SharableListMapping;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.SharableListService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SharableListServiceImpl implements SharableListService {

    private final GcVideoService videoService;
    private final GcAccessService accessService;
    private final PtChannelService channelService;
    private final GcSubjectService courseService;
    private final SharableListMapping sharableListMapping;
    private final PermitService permitService;

    @Override
    public GroupAccessDto getSharableListByContentId(Integer contentId, GcUser user) {
        GcVideo content = videoService.getVideoById(contentId);
        if (content.getOriginCourseId() != null) {
            return getSharableListByCourseId(content.getOriginCourseId(), user);
        }

        return getSharableListByChannelId(content.getOriginChannelId(), user);
    }

    @Override
    public GroupAccessDto getSharableListByChannelId(Integer channelId, GcUser user) {
        PtChannel channel = channelService.getById(channelId);

        if (channel == null) {
            log.error("Channel with id {} not found", channelId);
            throw new IllegalArgumentException("Channel not found");
        }

        GroupAccessDto groupAccessDto = new GroupAccessDto();
        Integer visibleFlag = channel.getVisibleFlag();

        if (isChannelPrivate(visibleFlag)) {
            groupAccessDto.setIsPrivate(true);
            return groupAccessDto;
        }

        if (isChannelPublic(visibleFlag)) {
            groupAccessDto.setIsPublic(true);
            return groupAccessDto;
        }

        groupAccessDto.setGroups(getChannelSharableGroups(channelId, channel.getMasterId(), user));
        return groupAccessDto;
    }

    private List<AccessGroupDetailsDto> getChannelSharableGroups(Integer channelId, Integer masterId, GcUser user) {
        if (permitService.isUserOrgAdmin(user.getUsername())) {
            return getAccessGroupDetailsDtos(accessService.getAccessByChannelId(masterId, channelId));
        }

        return getAccessGroupDetailsDtos(accessService.listAccess(null, masterId, user.getId()));
    }

    private boolean isChannelPublic(Integer visibleFlag) {
        return visibleFlag == 1;
    }

    private boolean isChannelPrivate(Integer visibleFlag) {
        return visibleFlag == 0;
    }

    @Override
    public GroupAccessDto getSharableListByCourseId(Integer courseId, GcUser user) {
        GcSubject course = courseService.getById(courseId);

        if (course == null) {
            log.error("Course with id {} not found", courseId);
            throw new IllegalArgumentException("Course not found");
        }

        GroupAccessDto groupAccessDto = new GroupAccessDto();
        if (isCoursePrivate(course)) {
            groupAccessDto.setIsPrivate(true);
            return groupAccessDto;
        }

        groupAccessDto.setGroups(getCourseSharableGroups(courseId, course.getMasterId(), user));
        return groupAccessDto;
    }

    private List<AccessGroupDetailsDto> getCourseSharableGroups(Integer courseId, Integer masterId, GcUser user) {
        if (permitService.isUserOrgAdmin(user.getUsername())) {
            return getAccessGroupDetailsDtos(accessService.getAccessBySubjectId(masterId, courseId));
        }

        return getAccessGroupDetailsDtos(accessService.listAccess(null, masterId, user.getId()));
    }

    private List<AccessGroupDetailsDto> getAccessGroupDetailsDtos(List<GcAccess> courseAcccessList) {
        return courseAcccessList.stream()
            .map(sharableListMapping::map)
            .collect(Collectors.toList());
    }

    private boolean isCoursePrivate(GcSubject course) {
        return course.getState() == 0;
    }
}
