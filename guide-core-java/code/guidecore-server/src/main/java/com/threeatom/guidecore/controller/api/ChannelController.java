package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.response.ChannelDto;
import com.threeatom.guidecore.service.GcUserService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Channel")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/channels", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelController {

    private final GcUserService userService;
    private final PtChannelService channelService;

    @GetMapping("/owned")
    @ApiOperation(value = "Get a list of channels owned by the current user")
    public List<ChannelDto> getSubscribedChannels(HttpServletRequest request) {
        Integer masterId = RequestUtil.getMasterId(request).orElseThrow();
        return channelService.getOwnerChannels(userService.getCurrentUser(request), masterId);
    }
}