package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitContentGroup;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.ContentGroupAction;
import com.threeatom.common.permissions.enums.ContentGroupRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class ContentGroupAuthorizationService
    extends ResourceAuthorizationService<PermitContentGroup, ContentGroupRole, ContentGroupAction> {

    private static final List<ContentGroupAction> CONTENT_GROUP_ACTIONS = List.of(
        ContentGroupAction.VIEW,
        ContentGroupAction.ADD_CONTENT,
        ContentGroupAction.MANAGE_CONTENT
    );
    private static final Map<ContentGroupRole, Map<ContentGroupAction, Predicate<PermitContentGroup>>>
        ROLE_CONTENT_GROUP_PERMISSIONS =
        Map.of(
            ContentGroupRole.VIEWER, Map.of(
                ContentGroupAction.VIEW, contentGroup -> true,
                ContentGroupAction.ADD_CONTENT, contentGroup -> false,
                ContentGroupAction.MANAGE_CONTENT, contentGroup -> false
            ),
            ContentGroupRole.ADMIN, Map.of(
                ContentGroupAction.VIEW, contentGroup -> true,
                ContentGroupAction.ADD_CONTENT, contentGroup -> true,
                ContentGroupAction.MANAGE_CONTENT, contentGroup -> true
            )
        );

    @Override
    public ContentGroupRole getRole(PermitUser permitUser, PermitContentGroup contentGroup) {
        if (permitUser.getManagedContentGroupIds().contains(contentGroup.getId())) {
            return ContentGroupRole.ADMIN;
        }

        if (permitUser.getContentGroupIds().contains(contentGroup.getId())) {
            return ContentGroupRole.VIEWER;
        }

        return null;
    }

    @Override
    protected Map<ContentGroupAction, Predicate<PermitContentGroup>> getPermissionMap(
        ContentGroupRole contentGroupRole) {
        return ROLE_CONTENT_GROUP_PERMISSIONS.get(contentGroupRole);
    }

    @Override
    protected List<ContentGroupAction> getActions() {
        return CONTENT_GROUP_ACTIONS;
    }
}
