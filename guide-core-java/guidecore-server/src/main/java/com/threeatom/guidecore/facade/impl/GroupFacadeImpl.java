package com.threeatom.guidecore.facade.impl;

import com.threeatom.common.exception.ForbiddenException;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupDto;
import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.facade.GroupFacade;
import com.threeatom.guidecore.mapping.ContentGroupMapping;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.ContentGroupChannelSubscriptionService;
import com.threeatom.guidecore.service.GcAccessService;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import com.threeatom.guidecore.service.GroupService;
import com.threeatom.guidecore.service.UserManagedGroupService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupFacadeImpl implements GroupFacade {

    private final UserManagedGroupService userManagedGroupService;
    private final AuthorizationService authorizationService;
    private final GcAccessService contentGroupService;
    private final ContentGroupMapping contentGroupMapping;
    private final GroupService groupService;
    private final GroupMapping groupMapping;
    private final GcContentGroupCourseAssignmentService courseAssignmentService;
    private final ContentGroupChannelSubscriptionService channelSubscriptionService;

    @Override
    public List<ContentGroupDto> getUserManagedContentGroups(PortalUser portalUser) {
        List<UserManagedGroup> userManagedGroups = userManagedGroupService.findUserManagedGroups(portalUser);
        List<String> userManagedGroupCodes = userManagedGroups.stream()
            .map(userManagedGroup -> userManagedGroup.getId().getPowtoonGroupCode())
            .collect(Collectors.toList());

        List<GcAccess> userManagedContentGroups = contentGroupService.findContentGroupsByCodes(userManagedGroupCodes);
        return contentGroupMapping.map(userManagedContentGroups);
    }

    @Override
    public GroupResponseDto groups(PortalUser portalUser) {
        List<Group> groups = groupService.findGroups(portalUser);
        List<String> codes = getGroupCodes(groups);

        return createGroupResponse(getGroupCodeToGroups(portalUser, groups, groupCodeToContentGroup(codes)));
    }

    @Override
    public void assignCourseToGroup(String groupCode, AssignCourseDto assignCourseDto, PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional = contentGroupService.findContentGroupsByCode(groupCode);
        contentGroupOptional.ifPresent(
            contentGroup -> {
                checkPermission(contentGroupOptional.get(), portalUser, PermitAction.MANAGE_CONTENT);
                courseAssignmentService.assignOrUpdateCourse(contentGroup.getId(), assignCourseDto,
                    portalUser.getUserId());
            });
    }

    @Override
    public void subscribeChannelToGroup(String groupCode, SubscribeChannelDto subscribeChannelDto,
                                        PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional = contentGroupService.findContentGroupsByCode(groupCode);
        contentGroupOptional.ifPresent(
            contentGroup -> {
                checkPermission(contentGroupOptional.get(), portalUser, PermitAction.MANAGE_CONTENT);
                channelSubscriptionService.subscribeOrUpdateChannels(portalUser, contentGroup.getId(),
                    subscribeChannelDto);
            });
    }

    @Override
    public Map<String, List<GroupCourseAssignmentDto>> groupCourseAssignments(String groupCode, PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional =
            contentGroupService.findContentGroupsByCodeAndMasterId(groupCode, portalUser.getMasterId());

        return contentGroupOptional.map(contentGroup -> {
                checkPermission(contentGroup, portalUser, PermitAction.VIEW);
                return courseAssignmentService.findByContentGroupId(contentGroup.getId());
            })
            .orElseGet(Map::of);
    }

    @Override
    public Map<String, List<GroupChannelSubscriptionDto>> groupChannelSubscriptions(String groupCode,
                                                                                    PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional =
            contentGroupService.findContentGroupsByCodeAndMasterId(groupCode, portalUser.getMasterId());

        return contentGroupOptional.map(contentGroup -> {
                checkPermission(contentGroup, portalUser, PermitAction.VIEW);
                return channelSubscriptionService.getContentGroupSubscriptions(contentGroup.getId());
            })
            .orElseGet(Map::of);
    }

    @Override
    public void removeCourseAssignment(String groupCode, Integer courseId, PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional =
            contentGroupService.findContentGroupsByCodeAndMasterId(groupCode, portalUser.getMasterId());

        contentGroupOptional.ifPresent(
            contentGroup -> {
                checkPermission(contentGroup, portalUser, PermitAction.MANAGE_CONTENT);
                courseAssignmentService.removeCourseAssignmentsByCourseId(List.of(courseId), contentGroup.getId());
            });
    }

    @Override
    public void removeChannelSubscription(String groupCode, Integer channelId, PortalUser portalUser) {
        Optional<GcAccess> contentGroupOptional =
            contentGroupService.findContentGroupsByCodeAndMasterId(groupCode, portalUser.getMasterId());

        contentGroupOptional.ifPresent(contentGroup -> {
            checkPermission(contentGroup, portalUser, PermitAction.MANAGE_CONTENT);
            channelSubscriptionService.removeChannelSubscriptions(List.of(contentGroup), List.of(channelId));
        });
    }

    private GroupResponseDto createGroupResponse(Map<String, GroupDto> groupDtos) {
        GroupResponseDto groupResponseDto = new GroupResponseDto();
        groupResponseDto.setGroups(groupDtos);
        return groupResponseDto;
    }

    private Map<String, GcAccess> groupCodeToContentGroup(List<String> codes) {
        return contentGroupService.findContentGroupsByCodes(codes).stream()
            .collect(Collectors.toMap(GcAccess::getCode, Function.identity()));
    }

    private Map<String, GroupDto> getGroupCodeToGroups(PortalUser portalUser, List<Group> groups,
                                                       Map<String, GcAccess> codeToContentGroups) {
        return groups.stream()
            .filter(group -> codeToContentGroups.containsKey(group.getPowtoonGroupCode()))
            .collect(Collectors.toMap(Group::getPowtoonGroupCode, group -> {
                GcAccess contentGroup = codeToContentGroups.get(group.getPowtoonGroupCode());
                return groupMapping.map(group, authorizationService.listPermissions(contentGroup, portalUser));
            }));
    }

    private List<String> getGroupCodes(List<Group> groups) {
        return groups.stream()
            .map(Group::getPowtoonGroupCode)
            .collect(Collectors.toList());
    }

    private void checkPermission(GcAccess contentGroup, PortalUser portalUser, PermitAction permitAction) {
        if (!authorizationService.checkAccess(contentGroup, permitAction, portalUser)) {
            log.error("User {} does not have permission to {} content group {}", portalUser.getUserId(),
                permitAction.name(), contentGroup.getGroupName());
            throw new ForbiddenException(
                "User does not have permission to %s content group".formatted(permitAction.name()));
        }
    }
}
