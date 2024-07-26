package com.threeatom.guidecore.service;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PtLoginConfig;

public interface PtApiClient {
    JSONObject getPowtoonPlayerPageData(String powtoonId, PtLoginConfig ptConfig) throws SystemException;
    JSONObject getPowtoonPlayerPageData(String powtoonId, String origin, String publicToken) throws SystemException;
    JSONObject getPowtoonPlayerPageData(String powtoonId, String origin) throws SystemException;
}

