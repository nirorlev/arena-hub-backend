package com.threeatom.common.permit.service;

import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;

public interface PermitService {

    boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId);

    boolean checkPermit(GcVideo video, String action, PortalUser portalUser);

    boolean checkPermit(GcAccess contentGroup, String action, PortalUser portalUser);

    boolean checkPermit(PtChannel channel, String action, PortalUser portalUser);

    boolean checkPermit(GcSubject course, String action, PortalUser portalUser);

    boolean checkPermit(GcUserSaveFolder playlist, String action, PortalUser portalUser);

}
