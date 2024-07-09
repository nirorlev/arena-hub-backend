package com.threeatom.guidecore.service;

import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;

/**
 * @author cvmcosta
 * @title: ExternalVideoProviderService
 * @description: Responsible for importing video information from a provider
 */
public interface ExternalVideoProviderService {
    JSONObject getVideoDataFromUrl(URL url) throws SystemException;
}

