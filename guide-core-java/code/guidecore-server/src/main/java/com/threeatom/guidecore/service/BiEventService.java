package com.threeatom.guidecore.service;

import com.threeatom.guidecore.enums.BiEvent;

public interface BiEventService {
    void publishEvent(BiEvent event);
}
