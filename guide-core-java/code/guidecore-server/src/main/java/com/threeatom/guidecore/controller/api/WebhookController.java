package com.threeatom.guidecore.controller.api;

import java.util.List;

import com.threeatom.common.controller.BaseController;
import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.dto.request.WebhookDto;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Webhooks API", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class WebhookController {

    

    @Autowired private PowtoonExternalVideoService powtoonExternalVideoService;
    @Autowired private SysFileService sysFileService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    private static final String POWTOON_PUBLISHED_TYPE = "powtoon_published";
    

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Update reactions")
    public ResponseEntity<Void> handleWebhook(@RequestBody @Valid WebhookDto webhook, HttpServletRequest request) {
        switch (webhook.getType()) {
            case POWTOON_PUBLISHED_TYPE:
                handlePowtoonPublishedWebhook(webhook);
                break;
            default:
                LOGGER.warn("Received unsupported webhook type: " + webhook.getType());
                break;
        }
        return ResponseEntity.ok().build();
    }

    private void handlePowtoonPublishedWebhook (WebhookDto webhook) {    
        String powtoonId = webhook.getData().getString("powtoon_id");
        if (powtoonId == null) {
            throw new SystemException("Missing powtoon_id field.");
        }
        LOGGER.info("Received powtoon published webhook. Powtoon ID: " + powtoonId);
        List<PowtoonExternalVideo> externalVideos = powtoonExternalVideoService.getByExternalId(powtoonId);
        for (PowtoonExternalVideo externalVideo: externalVideos) {
            SysFile sysFile = sysFileService.getById(externalVideo.getSysFileId());
            if (sysFile == null) {
                continue;
            }
            sysFileService.updateVideoInformation(sysFile, externalVideo);
            LOGGER.info("Updated sysFile for powtoon published webhook. SysFile ID: " + sysFile.getId());
        }
    }
}