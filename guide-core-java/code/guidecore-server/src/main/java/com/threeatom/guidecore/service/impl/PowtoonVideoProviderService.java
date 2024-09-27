package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.constant.EventUnifyType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PowtoonVideoProviderService implements ExternalVideoProviderService {

    private static final List<String> PLAIN_ID_URL_PREFIXES = List.of("online-presentation", "ws", "c");
    private static final String PUBLIC_TOKEN_PARAMETER = "public_link_token";
    private static final List<String> VALID_POWTOON_ROOT_DOMAINS = List.of("powtoon.com");
    private static final String KALTURA_PLAYER_URL_TEMPLATE = "https://www.kaltura.com/index.php/extwidget/preview/partner_id/%s/uiconf_id/%s/entry_id/%s/embed/dynamic?";
    private static final String MUX_PLAYER_URL_TEMPLATE = "https://stream.mux.com/%s.m3u8";
    private static final Integer PLAYER_PAGE_TYPE_INDEX = 1;
    private static final Integer VIDEO_ID_INDEX = 2;

    private static final Map<String, Integer> HOSTING_PROVIDER_TO_FILE_TYPE_INDEX = Map.of(
        "kaltura", EventUnifyType.POWTOON_KALTURA_FILE_TYPE_INDEX,
        "mux", EventUnifyType.POWTOON_MUX_FILE_TYPE_INDEX
    );

	private final PtApiClient ptApiClient;
    private final PtLoginConfigService ptLoginConfigService;

    private boolean isValidPowtoonUrl(URL url) {
        String host = url.getHost();
        return VALID_POWTOON_ROOT_DOMAINS.stream()
            .anyMatch(host::endsWith);
    }

    private String getPublicTokenFromURL(URL url) {
        String query = url.getQuery();
        if (query == null) {
            return null;
        }
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

    private String getPowtoonIdFromLoadedPage(URL url) {
        try {
            InputStream inputStream = url.openStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String chunk;
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

    private String buildKalturaPlayerUrl(String partnerId, String uiConfId, String entryId) {
        return String.format(KALTURA_PLAYER_URL_TEMPLATE, partnerId, uiConfId, entryId);
    }

    private String buildMuxPlayerUrl(String entryId) {
        return String.format(MUX_PLAYER_URL_TEMPLATE, entryId);
    }

    private Optional<String> buildPlayerUrl(Integer hostingProvider, JSONObject videoHosting) {
        String entryId = videoHosting.getString("id");
        if (hostingProvider == EventUnifyType.POWTOON_KALTURA_FILE_TYPE_INDEX) {
            String partnerId = videoHosting.getString("partner_id");
            String uiConfId = videoHosting.getString("ui_conf_id");
            return Optional.of(buildKalturaPlayerUrl(partnerId, uiConfId, entryId));
        }
        if (hostingProvider == EventUnifyType.POWTOON_MUX_FILE_TYPE_INDEX) {
            return Optional.of(buildMuxPlayerUrl(entryId));
        }
        return Optional.empty();
    }

    private JSONObject buildVideoData (JSONObject playerPageData) {
        JSONObject videoData = new JSONObject();
        videoData.put("title", playerPageData.getString("title"));
        videoData.put("description", playerPageData.getString("description"));
        videoData.put("duration", playerPageData.getFloat("video_duration"));
        videoData.put("thumbnailUrl", playerPageData.getString("thumb_url"));

        JSONObject videoHosting = playerPageData.getJSONObject("video_hosting");
        if (videoHosting == null) {
            videoData.put("hostingProvider", EventUnifyType.POWTOON_PENDING_FILE_TYPE_INDEX);
            return videoData;
        }
        String providerName = videoHosting.getString("provider");
        Integer hostingProvider = getHostingProviderIndexType(providerName);
        videoData.put("hostingProvider", hostingProvider);
        Optional<String> playerUrl = buildPlayerUrl(hostingProvider, videoHosting);
        playerUrl.ifPresent(url -> videoData.put("url", url));
        return videoData;
    }

    private Integer getHostingProviderIndexType(String provider) {
        return Optional.ofNullable(HOSTING_PROVIDER_TO_FILE_TYPE_INDEX.get(provider)).orElseThrow(() -> {
            log.error("Unsupported hosting provider received: {}", provider);
            return new SystemException("Unsupported hosting provider");
        });
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
        JSONObject videoData = buildVideoData(playerPageData);
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
