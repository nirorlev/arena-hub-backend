package com.threeatom.common.permit.service.impl;

import static java.lang.String.format;

import com.threeatom.common.exception.PermitException;
import com.threeatom.common.permit.enums.PermitAction;
import com.threeatom.common.permit.enums.PermitResource;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.config.PermitConfiguration;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.GcMasterService;
import io.permit.sdk.Permit;
import io.permit.sdk.PermitConfig;
import io.permit.sdk.api.PermitApiError;
import io.permit.sdk.api.PermitContextError;
import io.permit.sdk.enforcement.Resource;
import io.permit.sdk.enforcement.User;
import io.permit.sdk.openapi.models.TenantRead;
import io.permit.sdk.openapi.models.UserRead;
import java.io.IOException;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermitServiceImpl implements PermitService {

    private static final String IS_ORG_ADMIN_ATTRIBUTE = "isOrgAdmin";

    private static final String PERMIT_DEV_WIP_ENV_API_KEY =
        "permit_key_fJPWdxjlpLthYKoy8pKs7w9s6GgA1uSJgbo2IwktCYtbN40wz3wMggugaHXkAj6JOt4xp18shjJQrMh1WXVEvA";

    private final PermitConfiguration permitConfiguration;
    private final GcMasterService gcMasterService;
    private Permit permit;

    @PostConstruct
    public void init() {
        permit = new Permit(
            new PermitConfig.Builder(PERMIT_DEV_WIP_ENV_API_KEY)
                .withPdpAddress(permitConfiguration.getPdpAddress())
                .withDebugMode(true)
                .build()
        );
    }

    @Override
    public boolean isUserOrgAdmin(String username) {
        UserRead userRoles = null;
        try {
            userRoles = readUser(username);

            if (!CollectionUtils.isEmpty(userRoles.attributes)
                && userRoles.attributes.get(IS_ORG_ADMIN_ATTRIBUTE) != null) {
                return (boolean) userRoles.attributes.get(IS_ORG_ADMIN_ATTRIBUTE);
            }
        } catch (Exception e) {
            log.error("Exception when checking if user '{}' is org admin", username, e);
            throw new PermitException(500, format("Error checking if user '%s' is org admin", username), e);
        } catch (PermitApiError e) {
            log.error("Permit error when checking if user '{}' is org admin", username, e);
            throw new PermitException(
                format("Permit error occurred when checking user %s is org admin. ", username) + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean checkPermit(PermitResource resource, PermitAction action, GcUser user, Integer masterId) {
        if (user == null || masterId == null) {
            return false;
        }

        return checkPermit(resource, action, user.getUsername(), masterId);
    }

    private boolean checkPermit(PermitResource resource, PermitAction action, String username, Integer masterId) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }

        try {
            UserRead permitUser = readUser(username);
            Resource permitResource = getResource(resource, masterId);

            return permit.check(User.fromString(permitUser.key), action.getPermitAction(), permitResource);
        } catch (Exception e) {
            throw new PermitException(
                format("Error checking permission '%s' for resource '%s' and user '%s' from permit", resource, action,
                    username), e);
        } catch (PermitApiError e) {
            throw new PermitException(e.getMessage());
        }
    }

    private Resource getResource(PermitResource resource, Integer masterId)
        throws PermitContextError, PermitApiError, IOException {
        return new Resource.Builder(resource.getPermitValue())
            .withTenant(readTenant(masterId).key)
            .withAttributes(new HashMap<>())
            .build();
    }

    private TenantRead readTenant(String tenantId) throws PermitContextError, PermitApiError, IOException {
        return permit.api.tenants.get(tenantId);
    }

    private TenantRead readTenant(Integer masterId) throws PermitContextError, PermitApiError, IOException {
        GcMaster gcMaster = gcMasterService.getMasterById(masterId);
        return readTenant(gcMaster.getContext());
    }

    @Override
    public UserRead readUser(String username) throws PermitContextError, PermitApiError, IOException {
        return permit.api.users.get(username);
    }
}
