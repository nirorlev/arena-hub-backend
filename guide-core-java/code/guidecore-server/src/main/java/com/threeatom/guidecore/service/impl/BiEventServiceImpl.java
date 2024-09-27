package com.threeatom.guidecore.service.impl;

import com.threeatom.client.BiServiceClient;
import com.threeatom.guidecore.enums.BiEventType;
import com.threeatom.guidecore.service.BiEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiEventServiceImpl implements BiEventService {

    private final BiServiceClient biServiceClient;

    @Override
    public void send(BiEventType biEventType) {
        log.info("Sending event to BI system. Type: {}", biEventType);
        biServiceClient.sendEvent(biEventType);
    }
}
