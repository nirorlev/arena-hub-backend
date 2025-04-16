package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.request.SubscribeChannelDto;
import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import java.util.Map;

public interface GroupFacade {

    List<ContentGroupDto> getUserManagedContentGroups(PortalUser portalUser);

    GroupResponseDto groups(PortalUser portalUser);

    void assignCourseToGroup(String groupCode, AssignCourseDto assignCourseDto, PortalUser portalUser);

    void subscribeChannelToGroup(String groupCode, SubscribeChannelDto subscribeChannelDto, PortalUser portalUser);

    Map<String, List<GroupCourseAssignmentDto>> groupCourseAssignments(String groupCode, PortalUser portalUser);

    Map<String, List<GroupChannelSubscriptionDto>> groupChannelSubscriptions(String groupCode, PortalUser portalUser);

    void removeCourseAssignment(String groupCode, Integer courseId, PortalUser portalUser);

    void removeChannelSubscription(String groupCode, Integer channelId, PortalUser portalUser);
}
