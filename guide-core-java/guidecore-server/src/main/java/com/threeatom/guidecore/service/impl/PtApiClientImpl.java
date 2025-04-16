package com.threeatom.guidecore.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.client.PowtoonClient;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.PtApiClient;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PtApiClientImpl implements PtApiClient {

    private final PtOauthServiceImpl ptOauthServiceImpl;
    private final PowtoonClient powtoonClient;

    public JSONObject getPowtoonPlayerPageData(String powtoonId, PtLoginConfig ptConfig) throws SystemException {
        String accessToken = ptOauthServiceImpl.generateClientAccessToken(ptConfig);
        return powtoonClient.getPowtoonPlayerPageData(URI.create(ptConfig.getPtRootUrl()), accessToken, powtoonId);
    }

    public JSONObject getPowtoonPlayerPageData(String powtoonId, String origin, String publicToken)
        throws SystemException {
        return powtoonClient.getPublicPowtoonPlayerPageData(URI.create(origin), publicToken, powtoonId);
    }

    public JSONObject getPowtoonPlayerPageData(String powtoonId, String origin) throws SystemException {
        return powtoonClient.getPublicPowtoonPlayerPageData(URI.create(origin), powtoonId);
    }

}

