package com.threeatom.guidecore.listener;

import com.threeatom.client.BiServiceClient;
import com.threeatom.guidecore.dto.request.BiEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BiEventListener {

    private final BiServiceClient biServiceClient;

    @Async
    @EventListener
    public void handleBiReportEvent(BiEventDto event) {
        try {
            biServiceClient.send(event);
            log.info("Event sent to BI service: {}", event);
        } catch (Exception e) {
            log.error("Failed to send event to BI service", e);
            throw e;
        }
    }
}
