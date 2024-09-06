package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PortalUser;

public interface SharableListService {

    GroupAccessDto getSharableListByContentId(Integer contentId, PortalUser portalUser);

    GroupAccessDto getSharableListByChannelId(Integer channelId, PortalUser portalUser);

    GroupAccessDto getSharableListByCourseId(Integer id, PortalUser portalUser);

    GroupAccessDto getSharableListByPlaylistId(Integer id, Integer userId);
}
