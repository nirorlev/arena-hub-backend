package com.threeatom.guidecore.service;

import java.io.IOException;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PtLoginConfig;

/**
 * @author cvmcosta
 * @title: PtOauthService
 * @description: Responsible for performing oauth and generating access tokens
 * @date 2024/06/11
 */
public interface PtOauthService {
    String generateClientAccessToken(PtLoginConfig ptConfig) throws IOException, SystemException;
}

