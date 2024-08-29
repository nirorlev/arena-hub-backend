package com.threeatom.common.permit.service;

import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.guidecore.entity.GcUser;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.openapi.models.UserRead;
import java.io.IOException;

public interface PermitService {
    boolean isUserOrgAdmin(String username);

    boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId);

    UserRead readUser(String username) throws PermitContextError, PermitApiError, IOException;
}
