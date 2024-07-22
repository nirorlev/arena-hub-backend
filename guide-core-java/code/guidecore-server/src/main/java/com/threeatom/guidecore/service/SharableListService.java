package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.GroupAccessDto;
import com.threeatom.guidecore.entity.GcUser;

public interface SharableListService {

    GroupAccessDto getSharableListByContentId(Integer contentId, GcUser user);

    GroupAccessDto getSharableListByChannelId(Integer id, GcUser user);

    GroupAccessDto getSharableListByCourseId(Integer id, GcUser user);

    GroupAccessDto getSharableListByPlaylistId(Integer id, GcUser user);
}
