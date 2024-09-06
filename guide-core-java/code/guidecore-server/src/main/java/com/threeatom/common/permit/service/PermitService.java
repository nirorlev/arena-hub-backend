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
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.api.models.CreateOrUpdateResult;
import io.permit.sdk.openapi.models.RoleAssignmentRead;
import io.permit.sdk.openapi.models.TenantRead;
import io.permit.sdk.openapi.models.UserRead;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PermitService {
    boolean isUserOrgAdmin(String username);

    boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId);

    UserRead readUser(String username) throws PermitContextError, PermitApiError, IOException;

    boolean checkPermit(GcVideo video, String action, PortalUser portalUser);

    boolean checkPermit(GcAccess contentGroup, String action, PortalUser portalUser);

    boolean checkPermit(PtChannel channel, String action, PortalUser portalUser);

    boolean checkPermit(GcSubject course, String action, PortalUser portalUser);

    boolean checkPermit(GcUserSaveFolder playlist, String action, PortalUser portalUser);

    TenantRead readTenant(String key) throws PermitContextError, PermitApiError, IOException;

    TenantRead createTenant(String key, String name) throws PermitContextError, PermitApiError, IOException;

    CreateOrUpdateResult<UserRead> syncUser(GcUser user, Map<String, Object> userAttributes)
        throws PermitContextError, PermitApiError, IOException;

    List<RoleAssignmentRead> getAssignedRoles(String userKey, String tenantKey, int page, int size)
        throws PermitContextError, PermitApiError, IOException;

    void unassignRole(String userKey, String oldRole, String tenantKey)
        throws PermitContextError, PermitApiError, IOException;

    void assignRole(String userKey, String role, String tenantKey)
        throws PermitContextError, PermitApiError, IOException;
}
