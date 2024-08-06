package com.threeatom.guidecore.service;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PtLoginConfig;

public interface PtOauthService {
    String generateClientAccessToken(PtLoginConfig ptConfig) throws SystemException;
}

