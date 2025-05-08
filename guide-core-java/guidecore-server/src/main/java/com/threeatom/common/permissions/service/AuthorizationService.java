package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.enums.PortalAction;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.Course;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import java.util.Map;

public interface AuthorizationService {

    boolean checkAccess(GcVideo video, PermitAction action, PortalUser portalUser);

    boolean checkAccess(GcAccess contentGroup, PermitAction action, PortalUser portalUser);

    boolean checkAccess(PtChannel channel, PermitAction action, PortalUser portalUser);

    boolean checkAccess(Course course, PermitAction action, PortalUser portalUser);

    boolean checkAccess(GcUserSaveFolder playlist, PermitAction action, PortalUser portalUser);

    boolean checkAccess(PortalAction action, PortalUser portalUser);

    Map<String, Boolean> listPortalPermissions(PortalUser portalUser);

    Map<String, Boolean> listPermissions(Course course, PortalUser portalUser);

    Map<String, Boolean> listPermissions(GcUserSaveFolder playlist, PortalUser portalUser);

    Map<String, Boolean> listPermissions(PtChannel channel, PortalUser portalUser);

    Map<String, Boolean> listPermissions(GcVideo video, PortalUser portalUser);

    Map<String, Boolean> listPermissions(GcAccess contentGroup, PortalUser portalUser);
}
