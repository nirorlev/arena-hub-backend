package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.ChannelAction;
import com.threeatom.common.permissions.enums.ChannelRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ChannelAuthorizationService
    extends ResourceAuthorizationService<PermitChannel, ChannelRole, ChannelAction> {

    private static final List<ChannelAction> CHANNEL_ACTIONS = List.of(
        ChannelAction.CREATE,
        ChannelAction.DELETE,
        ChannelAction.VIEW,
        ChannelAction.EDIT,
        ChannelAction.SUBSCRIBE,
        ChannelAction.UNSUBSCRIBE,
        ChannelAction.SHARE,
        ChannelAction.ADD_CONTENT,
        ChannelAction.MANAGE_CONTENT,
        ChannelAction.PUBLISH
    );
    private static final Map<ChannelRole, Map<ChannelAction, Boolean>> ROLE_CHANNEL_PERMISSIONS = Map.of(
        ChannelRole.VIEWER, Map.of(
            ChannelAction.CREATE, false,
            ChannelAction.DELETE, false,
            ChannelAction.VIEW, true,
            ChannelAction.EDIT, false,
            ChannelAction.SUBSCRIBE, true,
            ChannelAction.UNSUBSCRIBE, true,
            ChannelAction.SHARE, true,
            ChannelAction.ADD_CONTENT, false,
            ChannelAction.MANAGE_CONTENT, false,
            ChannelAction.PUBLISH, false
        ),
        ChannelRole.ADMIN, Map.of(
            ChannelAction.CREATE, true,
            ChannelAction.DELETE, true,
            ChannelAction.VIEW, true,
            ChannelAction.EDIT, true,
            ChannelAction.SUBSCRIBE, true,
            ChannelAction.UNSUBSCRIBE, true,
            ChannelAction.SHARE, true,
            ChannelAction.ADD_CONTENT, true,
            ChannelAction.MANAGE_CONTENT, true,
            ChannelAction.PUBLISH, true
        )
    );

    @Override
    protected Map<ChannelAction, Boolean> getPermissionMap(ChannelRole role) {
        return ROLE_CHANNEL_PERMISSIONS.get(role);
    }

    @Override
    protected List<ChannelAction> getActions() {
        return CHANNEL_ACTIONS;
    }

    @Override
    public ChannelRole getRole(PermitUser permitUser, PermitChannel channel) {
        if (String.valueOf(permitUser.getId()).equals(channel.getOwnerId())
            || permitUser.isOrgAdmin() && !channel.isPrivate()) {
            return ChannelRole.ADMIN;
        }

        if (channel.isPublic() || userHasChannelInContentGroups(permitUser, channel)) {
            return ChannelRole.VIEWER;
        }

        return null;
    }

    private boolean userHasChannelInContentGroups(PermitUser permitUser, PermitChannel channel) {
        Set<String> allUserContentGroupIds = permitUser.getContentGroupIds();
        allUserContentGroupIds.addAll(permitUser.getManagedContentGroupIds());

        return allUserContentGroupIds.stream()
            .anyMatch(userContentGroupId -> channel.getContentGroupIds().contains(userContentGroupId));
    }
}
