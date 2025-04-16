package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.ValidationException;
import com.threeatom.guidecore.dto.response.AccessGroupDetailsDto;
import com.threeatom.guidecore.dto.response.AccessSourceDto;
import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.enums.SourceType;
import com.threeatom.guidecore.mapping.SharableListMapping;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.GcVideoService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.SharableListService;
import java.util.Collections;
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
    private final GcUserSaveFolderService playlistService;

    @Override
    public GroupAccessDto getSharableListByContentId(Integer contentId, PortalUser portalUser) {
        GcVideo content = videoService.getVideoById(contentId);
        if (content.getOriginCourseId() != null) {
            return getSharableListByCourseId(content.getOriginCourseId(), portalUser);
        }

        return getSharableListByChannelId(content.getOriginChannelId(), portalUser);
    }

    @Override
    public GroupAccessDto getSharableListByChannelId(Integer channelId, PortalUser portalUser) {
        PtChannel channel = channelService.findById(channelId);

        AccessSourceDto accessSourceDto =
            getAccessSourceDto(channelId, channel.getCreateUserId(), SourceType.CHANNEL, portalUser.getUserId());
        accessSourceDto.setSlug(channel.getChannelSlug());
        if (channel.isPrivate()) {
            return getGroupAccessDto(false, true, Collections.emptyList(), accessSourceDto);
        }

        if (channel.isPublic()) {
            return getGroupAccessDto(true, false, Collections.emptyList(), accessSourceDto);
        }

        List<AccessGroupDetailsDto> groups = getChannelSharableGroups(channelId, channel.getMasterId(), portalUser);
        return getGroupAccessDto(false, false, groups, accessSourceDto);
    }

    @Override
    public GroupAccessDto getSharableListByCourseId(Integer courseId, PortalUser portalUser) {
        GcSubject course = courseService.getById(courseId);

        if (course == null) {
            log.error("Course with id {} not found", courseId);
            throw new ValidationException("Course not found");
        }

        AccessSourceDto accessSourceDto = getAccessSourceDto(courseId, course.getCreateUser(), SourceType.COURSE,
            portalUser.getUserId());
        if (isCoursePrivate(course)) {
            return getGroupAccessDto(false, true, Collections.emptyList(), accessSourceDto);
        }

        List<AccessGroupDetailsDto> groups = getCourseSharableGroups(courseId, course.getMasterId(), portalUser);
        return getGroupAccessDto(false, false, groups, accessSourceDto);
    }

    @Override
    public GroupAccessDto getSharableListByPlaylistId(Integer playlistId, Integer userId) {
        GcUserSaveFolder playlist = playlistService.getById(playlistId);
        AccessSourceDto accessSourceDto =
            getAccessSourceDto(playlistId, playlist.getUserId(), SourceType.PLAYLIST, userId);

        return getGroupAccessDto(!playlist.getIsPrivate(), playlist.getIsPrivate(), null, accessSourceDto);
    }

    private List<AccessGroupDetailsDto> getChannelSharableGroups(Integer channelId, Integer masterId,
                                                                 PortalUser portalUser) {
        if (portalUser.isOrgAdmin()) {
            return getAccessGroupDetailsDtos(accessService.getAccessByChannelId(masterId, channelId));
        }

        return getAccessGroupDetailsDtos(accessService.listAccess(null, masterId, portalUser.getUserId()));
    }

    private GroupAccessDto getGroupAccessDto(boolean isPublic, boolean isPrivate, List<AccessGroupDetailsDto> groups) {
        GroupAccessDto groupAccessDto = new GroupAccessDto();
        groupAccessDto.setIsPrivate(isPrivate);
        groupAccessDto.setIsPublic(isPublic);
        groupAccessDto.setGroups(groups);
        return groupAccessDto;
    }

    private GroupAccessDto getGroupAccessDto(boolean isPublic, boolean isPrivate, List<AccessGroupDetailsDto> groups,
                                             AccessSourceDto source) {
        GroupAccessDto groupAccessDto = getGroupAccessDto(isPublic, isPrivate, groups);
        groupAccessDto.setSource(source);
        return groupAccessDto;
    }

    private AccessSourceDto getAccessSourceDto(Integer channelId, Integer createUserId,
                                               SourceType sourceType, Integer userId) {
        AccessSourceDto accessSourceDto = new AccessSourceDto();
        accessSourceDto.setId(String.valueOf(channelId));
        accessSourceDto.setType(sourceType.toString());
        accessSourceDto.setCanPublish(userId.equals(createUserId));
        return accessSourceDto;
    }

    private List<AccessGroupDetailsDto> getCourseSharableGroups(Integer courseId, Integer masterId,
                                                                PortalUser portalUser) {
        if (portalUser.isOrgAdmin()) {
            return getAccessGroupDetailsDtos(accessService.getAccessBySubjectId(masterId, courseId));
        }

        return getAccessGroupDetailsDtos(accessService.listAccess(null, masterId, portalUser.getUserId()));
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
