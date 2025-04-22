package com.threeatom.client;


import com.alibaba.fastjson.JSONObject;
import com.threeatom.client.dto.PowtoonAuthDto;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.client.dto.request.GetTokenDto;
import com.threeatom.client.dto.request.LogOutDto;
import com.threeatom.config.PowtoonClientConfiguration;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
import java.net.URI;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "powtoon", url = "https://runtime-placeholder.com", configuration = PowtoonClientConfiguration.class)
public interface PowtoonClient {

    @GetMapping("/api/v1.0/integrations/hub/users/me")
    PowtoonUserDto getUserInfo(URI baseUrl, @RequestHeader("Authorization") String token);

    @GetMapping
    PtGroupsVo getGroups(URI baseUrl, @RequestHeader("Authorization") String token);

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void logOut(URI baseUrl, @RequestBody LogOutDto body);

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    PowtoonAuthDto getAuthToken(URI baseUrl, @RequestBody GetTokenDto body);

    @GetMapping("/api/v2/powtoons/{powtoonId}/player-page")
    JSONObject getPowtoonPlayerPageData(URI baseUrl, @RequestHeader("Authorization") String token,
                                        @PathVariable("powtoonId") String powtoonId);

    @GetMapping("/api/v2/powtoons/{powtoonId}/player-page")
    JSONObject getPublicPowtoonPlayerPageData(URI baseUrl, @RequestHeader("X-Public-Link-Token") String token,
                                              @PathVariable("powtoonId") String powtoonId);

    @GetMapping("/api/v2/powtoons/{powtoonId}/player-page")
    JSONObject getPublicPowtoonPlayerPageData(URI baseUrl, @PathVariable("powtoonId") String powtoonId);
}
