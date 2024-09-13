package com.threeatom.common.permit.service;

import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;

public interface PermitService {

    boolean checkPermit(GcVideo video, PermitAction action, PortalUser portalUser);

    boolean checkPermit(GcAccess contentGroup, PermitAction action, PortalUser portalUser);

    boolean checkPermit(PtChannel channel, PermitAction action, PortalUser portalUser);

    boolean checkPermit(GcSubject course, PermitAction action, PortalUser portalUser);

    boolean checkPermit(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser);

    void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser);

    void populatePermissions(PtChannel channel, PortalUser portalUser);

    void populatePermissions(GcVideo video, PortalUser portalUser);

    boolean checkMenuItem(String menuItemKey, PortalUser portalUser);

}
