package com.threeatom.guidecore.service;

import java.net.URL;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;

public interface ExternalVideoProviderService {
    JSONObject getVideoDataFromUrl(URL url) throws SystemException;
}

