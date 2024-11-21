package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitChannel;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.ChannelAction;
import com.threeatom.common.permissions.enums.ChannelRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
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
    private static final Map<ChannelRole, Map<ChannelAction, Predicate<PermitChannel>>> ROLE_CHANNEL_PERMISSIONS =
        Map.of(
            ChannelRole.VIEWER, Map.of(
                ChannelAction.CREATE, channel -> false,
                ChannelAction.DELETE, channel -> false,
                ChannelAction.VIEW, channel -> true,
                ChannelAction.EDIT, channel -> false,
                ChannelAction.SUBSCRIBE, channel -> true,
                ChannelAction.UNSUBSCRIBE, channel -> true,
                ChannelAction.SHARE, channel -> true,
                ChannelAction.ADD_CONTENT, channel -> false,
                ChannelAction.MANAGE_CONTENT, channel -> false,
                ChannelAction.PUBLISH, channel -> false
            ),
            ChannelRole.ADMIN, Map.of(
                ChannelAction.CREATE, channel -> true,
                ChannelAction.DELETE, channel -> true,
                ChannelAction.VIEW, channel -> true,
                ChannelAction.EDIT, channel -> true,
                ChannelAction.SUBSCRIBE, channel -> true,
                ChannelAction.UNSUBSCRIBE, channel -> true,
                ChannelAction.SHARE, channel -> true,
                ChannelAction.ADD_CONTENT, channel -> true,
                ChannelAction.MANAGE_CONTENT, channel -> true,
                ChannelAction.PUBLISH, channel -> true
            )
        );

    @Override
    protected Map<ChannelAction, Predicate<PermitChannel>> getPermissionMap(ChannelRole role) {
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
