package com.threeatom.guidecore.service.impl;

import com.threeatom.client.BiServiceClient;
import com.threeatom.guidecore.enums.BiEventType;
import com.threeatom.guidecore.service.BiEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BiEventServiceImpl implements BiEventService {

    private final BiServiceClient biServiceClient;

    @Override
    public void send(BiEventType biEventType) {
        biServiceClient.sendEvent(biEventType);
    }
}
