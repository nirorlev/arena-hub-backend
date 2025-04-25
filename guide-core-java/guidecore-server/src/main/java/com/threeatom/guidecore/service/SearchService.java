package com.threeatom.guidecore.service;

import com.threeatom.common.controller.Message;
import com.threeatom.guidecore.dto.request.SearchDto;
import com.threeatom.guidecore.entity.PortalUser;

public interface SearchService {

    Message search(SearchDto searchDto, PortalUser portalUser);
}
