package com.threeatom.guidecore.service;

import com.threeatom.guidecore.enums.BiEventAction;

public interface BiEventService {
    void publishEvent(BiEventAction action, Integer userId);
}
