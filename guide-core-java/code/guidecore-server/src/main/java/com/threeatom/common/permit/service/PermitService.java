package com.threeatom.common.permit.service;

import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.guidecore.entity.GcUser;

public interface PermitService {
    boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId);
}
