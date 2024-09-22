package com.threeatom.common.permissions.service;

import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcSubject;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;

public interface AuthorizationService {

    boolean checkAccess(GcVideo video, PermitAction action, PortalUser portalUser);

    boolean checkAccess(GcAccess contentGroup, PermitAction action, PortalUser portalUser);

    boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser);

    boolean checkAccess(GcSubject course, PermitAction action, PortalUser portalUser);

    boolean checkAccess(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser);

    void populatePermissions(GcUserSaveFolder playlist, PortalUser portalUser);

    void populatePermissions(PtChannel channel, PortalUser portalUser);

    void populatePermissions(GcVideo video, PortalUser portalUser);

    boolean checkMenuItem(String menuItemKey, PortalUser portalUser);

}
