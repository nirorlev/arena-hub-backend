package com.threeatom.client;


import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.guidecore.controller.user.vo.PtGroupsVo;
import java.net.URI;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "powtoon", url = "https://runtime-placeholder.com")
public interface PowtoonClient {

    @GetMapping("/api/v1.0/integrations/hub/users/me")
    PowtoonUserDto getUserInfo(URI baseUrl, @RequestHeader("Authorization") String token);

    @GetMapping
    PtGroupsVo getGroups(URI baseUrl, @RequestHeader("Authorization") String token);

    @PostMapping(produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    PtGroupsVo logOut(URI baseUrl, @RequestBody Map<String, String> body);
}
