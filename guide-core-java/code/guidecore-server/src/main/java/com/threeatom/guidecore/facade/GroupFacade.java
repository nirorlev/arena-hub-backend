package com.threeatom.guidecore.facade;

import com.threeatom.guidecore.dto.request.AssignChannelDto;
import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.GroupChannelSubscriptionDto;
import com.threeatom.guidecore.dto.response.GroupCourseAssignmentDto;
import com.threeatom.guidecore.dto.response.ContentGroupDto;
import com.threeatom.guidecore.dto.response.GroupResponseDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface GroupFacade {

    List<ContentGroupDto> getUserManagedContentGroups(PortalUser portalUser);

    GroupResponseDto groups(PortalUser portalUser);

    void assignCourseToGroup(String groupCode, AssignCourseDto assignCourseDto, GcUser currentUser);

    void subscribeChannelToGroup(String groupCode, AssignChannelDto assignChannelDto, PortalUser portalUser);

    List<GroupCourseAssignmentDto> groupCourseAssignments(String groupCode, PortalUser portalUser,
                                                          HttpServletRequest request);

    List<GroupChannelSubscriptionDto> groupChannelSubscriptions(String groupCode, PortalUser portalUser,
                                                                HttpServletRequest request);

    void removeChannelSubscription(String groupCode, Integer channelId, PortalUser portalUser);

    void removeCourseAssignment(String groupCode, Integer courseId, PortalUser portalUser);
}
