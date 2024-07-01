package com.threeatom.guidecore.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.PtApiClient;
import com.threeatom.utils.HttpUtil;

/**
 * @author cvmcosta
 * @title: PtOauthService
 * @projectName jeeplus
 * @description: Responsible for communicating with the Powtoon API
 * @date 2024/06/11
 */
@Service
public class PtApiClientImpl implements PtApiClient {

    private static final String PLAYER_PAGE_DATA_API_ENDPOINT = "/api/v2/powtoons/%s/player-page";
    private static final String PUBLIC_TOKEN_HEADER_NAME = "X-Public-Link-Token";

    @Autowired
	private PtOauthServiceImpl ptOauthServiceImpl;

    public JSONObject getPowtoonPlayerPageData(String powtoonId, PtLoginConfig ptConfig) throws SystemException {
        String accessToken = ptOauthServiceImpl.generateClientAccessToken(ptConfig);
        Header[] headers = {new BasicHeader("Authorization", "Bearer " + accessToken)};
        String url = ptConfig.getPtRootUrl() + String.format(PLAYER_PAGE_DATA_API_ENDPOINT, powtoonId);
        JSONObject response = HttpUtil.get(url, headers);
        return response;
    }

    public JSONObject getPowtoonPlayerPageData(String powtoonId, String origin, String publicToken) throws SystemException {
        Header[] headers = {new BasicHeader(PUBLIC_TOKEN_HEADER_NAME, publicToken)};
        String url = origin + String.format(PLAYER_PAGE_DATA_API_ENDPOINT, powtoonId);
        JSONObject response = HttpUtil.get(url, headers);
        return response;
    }

    public JSONObject getPowtoonPlayerPageData(String powtoonId, String origin) throws SystemException {
        String url = origin + String.format(PLAYER_PAGE_DATA_API_ENDPOINT, powtoonId);
        JSONObject response = HttpUtil.get(url);
        return response;
    }

}

