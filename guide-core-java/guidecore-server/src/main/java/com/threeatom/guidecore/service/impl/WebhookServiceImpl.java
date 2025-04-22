package com.threeatom.guidecore.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.dto.request.WebhookDto;
import com.threeatom.guidecore.entity.PowtoonExternalVideo;
import com.threeatom.guidecore.enums.WebhookType;
import com.threeatom.guidecore.service.PowtoonExternalVideoService;
import com.threeatom.guidecore.service.WebhookService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.service.SysFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final PowtoonExternalVideoService powtoonExternalVideoService;
    private final SysFileService sysFileService;

    @Override
    public void handleWebhook(WebhookDto webhook) {
        Optional<WebhookType> webhookType = WebhookType.fromString(webhook.getType());
        if (webhookType.isEmpty()){
            log.warn("Received unsupported webhook type: " + webhook.getType());
            return;
        }
        switch (webhookType.get()) {
            case POWTOON_PUBLISHED:
                handlePowtoonPublishedWebhook(webhook);
                break;
            default:
                break;
        }
    }

    private void handlePowtoonPublishedWebhook (WebhookDto webhook) {    
        String powtoonId = webhook.getData().getString("powtoon_id");
        if (powtoonId == null) {
            throw new SystemException("Missing powtoon_id field.");
        }
        log.info("Received powtoon published webhook. Powtoon ID: " + powtoonId);
        List<PowtoonExternalVideo> externalVideos = powtoonExternalVideoService.getByExternalId(powtoonId);
        for (PowtoonExternalVideo externalVideo: externalVideos) {
            SysFile sysFile = sysFileService.getById(externalVideo.getSysFileId());
            if (sysFile == null) {
                continue;
            }
            sysFileService.updateVideoInformation(sysFile, externalVideo);
            log.info("Updated sysFile for powtoon published webhook. SysFile ID: " + sysFile.getId());
        }
    }
    
}
