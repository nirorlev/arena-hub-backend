package com.threeatom.common.permissions.service;

import com.threeatom.common.permissions.dto.PermitResource;
import com.threeatom.common.permissions.dto.PermitUser;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public abstract class ResourceAuthorizationService<
    RESOURCE extends PermitResource, ROLE extends Enum<ROLE>, ACTION extends Enum<ACTION>> {

    public abstract ROLE getRole(PermitUser permitUser, RESOURCE resource);

    protected abstract Map<ACTION, Boolean> getPermissionMap(ROLE role);

    protected abstract List<ACTION> getActions();

    public Map<ACTION, Boolean> listPermissions(PermitUser permitUser, RESOURCE resource) {
        ROLE role = getRole(permitUser, resource);
        return role == null ? deniedPermissions() : getPermissionMap(role);
    }

    public boolean checkPermissions(PermitUser permitUser, ACTION action, RESOURCE resource) {
        return listPermissions(permitUser, resource).getOrDefault(action, false);
    }

    protected Map<ACTION, Boolean> deniedPermissions() {
        return getActions().stream().collect(Collectors.toMap(Function.identity(), action -> false));
    }
}
