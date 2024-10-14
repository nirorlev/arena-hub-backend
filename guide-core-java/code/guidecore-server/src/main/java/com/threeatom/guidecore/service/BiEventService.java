package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.enums.BiEventAction;

public interface BiEventService {
    void publishEvent(BiEventAction event, GcUser user);
}
