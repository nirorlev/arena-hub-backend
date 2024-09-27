package com.threeatom.guidecore.service;

import com.threeatom.guidecore.enums.BiEventType;

public interface BiEventService {

    void send(BiEventType biEventType);
}
