package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.GroupAccessDto;

public interface SharableListService {

    GroupAccessDto getSharableListByContentId(Integer contentId, Integer userId);
}
