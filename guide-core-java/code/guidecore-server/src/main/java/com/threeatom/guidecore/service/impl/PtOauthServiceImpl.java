package com.threeatom.guidecore.service.impl;

import java.io.IOException;
import java.util.Base64;

import org.apache.http.Header;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.threeatom.common.exception.SystemException;
import com.threeatom.common.redis.RedisOperator;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.PtOauthService;
import com.threeatom.utils.HttpUtil;

/**
 * @author cvmcosta
 * @title: PtOauthService
 * @projectName jeeplus
 * @description: Responsible for performing oauth and generating access tokens
 * @date 2024/06/11
 */
@Service
public class PtOauthServiceImpl implements PtOauthService {

    private static final String CLIENT_ACCESS_TOKEN_CACHE_PREFIX = "PT_client_access_token:";

    @Autowired
	private RedisOperator redisOperator;

    public String generateClientAccessToken(PtLoginConfig ptConfig) throws IOException, SystemException {
        String cacheKey = CLIENT_ACCESS_TOKEN_CACHE_PREFIX + ptConfig.getMasterId();
        String cachedToken = (String) redisOperator.get(cacheKey);
        if (cachedToken != null) return cachedToken;

        String clientId = ptConfig.getVideoServiceClientId();
        String secret = ptConfig.getVideoServiceSecret();
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes());
        Header[] headers = {new BasicHeader("Authorization", "Basic " + credentials)};

        String url = ptConfig.getPtRootUrl() + ptConfig.getOauthToken();
        String body = "grant_type=client_credentials";
        JSONObject response = HttpUtil.post(url, body, ContentType.APPLICATION_FORM_URLENCODED, headers);
        if (response == null) throw new SystemException("Failed retrieving Powtoon access token");

        String accessToken = response.getString("access_token");
        Long expiresIn = response.getLong("expires_in");
        redisOperator.set(cacheKey, accessToken, expiresIn);
        return accessToken;
    }
}

