package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.FrontendVersionOverrideDto;

public interface FrontendVersionService {

    FrontendVersionOverrideDto getVersion(String requestedVersion, String remoteHost);
}