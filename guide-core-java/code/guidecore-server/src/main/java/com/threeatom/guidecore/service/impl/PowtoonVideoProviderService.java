package com.threeatom.guidecore.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.entity.PtLoginConfig;
import com.threeatom.guidecore.service.PtApiClient;
import com.threeatom.guidecore.service.PtLoginConfigService;

import lombok.RequiredArgsConstructor;

import com.threeatom.guidecore.service.ExternalVideoProviderService;

/**
 * @author cvmcosta
 * @title: PowtoonExternalVideoProviderService
 * @projectName jeeplus
 * @description: Responsible for importing video information from Powtoon
 */
@Service
@RequiredArgsConstructor
public class PowtoonVideoProviderService implements ExternalVideoProviderService {

    private static final List<String> PLAIN_ID_URL_PREFIXES = Arrays.asList("online-presentation", "ws", "c");
    private static final String PUBLIC_TOKEN_PARAMETER = "public_link_token";
    private static final List<String> VALID_POWTOON_ROOT_DOMAINS = Arrays.asList("powtoon.com");
    private static final String PLAYER_URL_TEMPLATE = "https://www.kaltura.com/index.php/extwidget/preview/partner_id/%s/uiconf_id/%s/entry_id/%s/embed/dynamic?";
    private static final Integer PLAYER_PAGE_TYPE_INDEX = 1;
    private static final Integer VIDEO_ID_INDEX = 2;

	private final PtApiClient ptApiClient;
    private final PtLoginConfigService ptLoginConfigService;

    private boolean isValidPowtoonUrl(URL url) {
        String host = url.getHost();
        return VALID_POWTOON_ROOT_DOMAINS.stream()
            .anyMatch(host::endsWith);
    }

    private String getPublicTokenFromURL(URL url) {
        String query = url.getQuery();
        String[] parameters = query.split("&");
        for (String parameter : parameters) {
            String[] parameterParts = parameter.split("=");
            if (parameterParts[0].equals(PUBLIC_TOKEN_PARAMETER)) {
                return parameterParts[1];
            }
        }
        return null;
    }

    private PtLoginConfig getPtConfig(String origin) {
        QueryWrapper<PtLoginConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pt_root_url", origin);
        return ptLoginConfigService.getOne(queryWrapper);
    }

    // TODO: Refactor this piece of code
    private String getPowtoonIdFromLoadedPage(URL url) {
        try {
            InputStream inputStream = url.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String chunk = null;
            String videoId = null;
            while ((chunk = bufferedReader.readLine()) != null) {
                System.out.println(chunk + "<---------------->");
                if (chunk.contains("canonical")) {
                    // Get the web element video link
                    String startStr = "href=\"";
                    String endStr = "\">";
                    int startIndex = chunk.indexOf(startStr) + startStr.length();
                    int endIndex = chunk.indexOf(endStr, startIndex);
                    String presentationUrl = chunk.substring(startIndex, endIndex);
                    // Parse video ID
                    String subString = presentationUrl.substring(presentationUrl.indexOf("online-presentation/") + "online-presentation/".length());
                    videoId = subString.substring(0, subString.indexOf("/"));
                }
            }
            return videoId;
        } catch (IOException e) {
            throw new SystemException("Error while loading powtoon player page");
        }
    }   

    private String getPowtoonIdFromUrl (URL url) {
        String path = url.getPath();
        String[] pathParts = path.split("/");
        // If the first part of the URL (excluding the initial empty position) is one of the
        //  predetermined plain ID prefixes. Ex: /online-presentation/{powtoon_id}/...
        if (PLAIN_ID_URL_PREFIXES.contains(pathParts[PLAYER_PAGE_TYPE_INDEX])) {
            return pathParts[VIDEO_ID_INDEX];
        }
        return getPowtoonIdFromLoadedPage(url);
    }

    private String buildPlayerUrl (String partnerId, String uiConfId, String entryId) {
        return String.format(PLAYER_URL_TEMPLATE, partnerId, uiConfId, entryId);
    }

    private JSONObject buildVideoData (JSONObject playerPageData, JSONObject videoHosting) {
        JSONObject videoData = new JSONObject();
        String partnerId = videoHosting.getString("partner_id");
        String uiConfId = videoHosting.getString("ui_conf_id");
        String entryId = videoHosting.getString("id");
        videoData.put("title", playerPageData.getString("title"));
        videoData.put("description", playerPageData.getString("description"));
        videoData.put("duration", playerPageData.getFloat("video_duration"));
        videoData.put("thumbnailUrl", playerPageData.getString("thumb_url"));
        videoData.put("playerUrl", buildPlayerUrl(partnerId, uiConfId, entryId));
        return videoData;
    }

    private JSONObject buildSourceData (String powtoonId, String origin, String publicToken, JSONObject playerPageData) {
        JSONObject sourceData = new JSONObject();
        sourceData.put("id", powtoonId);
        sourceData.put("origin", origin);
        sourceData.put("version", playerPageData.getString("version"));
        sourceData.put("publicToken", publicToken);
        return sourceData;
    }

    private JSONObject formatData (String powtoonId, String origin, String publicToken, JSONObject playerPageData) {
        JSONObject videoHosting = playerPageData.getJSONObject("video_hosting");
        if (videoHosting == null) {
            throw new SystemException("Video data does not contain video hosting information");
        }
        JSONObject videoData = buildVideoData(playerPageData, videoHosting);
        JSONObject source = buildSourceData(powtoonId, origin, publicToken, playerPageData);
        videoData.put("source", source);
        return videoData;
    }

    private JSONObject getPlayerPageData(String powtoonId, String origin, String publicToken) {
        if (publicToken != null) {
            return ptApiClient.getPowtoonPlayerPageData(powtoonId, origin, publicToken);
        }
        PtLoginConfig ptConfig = getPtConfig(origin);
        if (ptConfig != null) {
            return ptApiClient.getPowtoonPlayerPageData(powtoonId, ptConfig);
        }
        return ptApiClient.getPowtoonPlayerPageData(powtoonId, origin);
    }

    @Override
    public JSONObject getVideoDataFromUrl(URL url) throws SystemException {
        if (!isValidPowtoonUrl(url)) {
            throw new SystemException("Invalid Powtoon URL");
        }
        String powtoonId = getPowtoonIdFromUrl(url);
        String publicToken = getPublicTokenFromURL(url);
        String origin = url.getProtocol() + "://" + url.getHost();
        JSONObject playerPageData = getPlayerPageData(powtoonId, origin, publicToken);
        return formatData(powtoonId, origin, publicToken, playerPageData);
    }

    public JSONObject getVideoDataFromExternalVideo(PowtoonExternalVideo externalVideo) throws SystemException {
        String publicToken = externalVideo.getPublicToken();
        String powtoonId = externalVideo.getExternalId();
        String origin = externalVideo.getOrigin();
        JSONObject playerPageData = getPlayerPageData(powtoonId, origin, publicToken);
        return formatData(powtoonId, origin, publicToken, playerPageData);
    }
}
