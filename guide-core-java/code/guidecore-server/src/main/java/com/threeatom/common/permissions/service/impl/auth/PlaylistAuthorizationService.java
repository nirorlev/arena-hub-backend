package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitPlaylist;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.PlaylistAction;
import com.threeatom.common.permissions.enums.PlaylistRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PlaylistAuthorizationService
    extends ResourceAuthorizationService<PermitPlaylist, PlaylistRole, PlaylistAction> {

    private static final List<PlaylistAction> PLAYLIST_ACTIONS = List.of(
        PlaylistAction.VIEW,
        PlaylistAction.CREATE,
        PlaylistAction.DELETE,
        PlaylistAction.EDIT,
        PlaylistAction.ADD_CONTENT,
        PlaylistAction.MANAGE_CONTENT,
        PlaylistAction.PUBLISH,
        PlaylistAction.SUBSCRIBE,
        PlaylistAction.UNSUBSCRIBE
    );
    private static final Map<PlaylistRole, Map<PlaylistAction, Boolean>> ROLE_PLAYLIST_PERMISSIONS = Map.of(
        PlaylistRole.VIEWER, Map.of(
            PlaylistAction.CREATE, false,
            PlaylistAction.DELETE, false,
            PlaylistAction.VIEW, true,
            PlaylistAction.EDIT, false,
            PlaylistAction.SUBSCRIBE, true,
            PlaylistAction.UNSUBSCRIBE, true,
            PlaylistAction.ADD_CONTENT, false,
            PlaylistAction.MANAGE_CONTENT, false,
            PlaylistAction.PUBLISH, false
        ),
        PlaylistRole.ADMIN, Map.of(
            PlaylistAction.CREATE, true,
            PlaylistAction.DELETE, true,
            PlaylistAction.VIEW, true,
            PlaylistAction.EDIT, true,
            PlaylistAction.SUBSCRIBE, true,
            PlaylistAction.UNSUBSCRIBE, true,
            PlaylistAction.ADD_CONTENT, true,
            PlaylistAction.MANAGE_CONTENT, true,
            PlaylistAction.PUBLISH, true
        )
    );

    @Override
    public PlaylistRole getRole(PermitUser permitUser, PermitPlaylist playlist) {
        if (permitUser.getId().equals(playlist.getOwnerId()) || permitUser.isOrgAdmin() && !playlist.isPrivate()) {
            return PlaylistRole.ADMIN;
        }

        if (playlist.isPublic()) {
            return PlaylistRole.VIEWER;
        }

        return null;
    }

    @Override
    protected Map<PlaylistAction, Boolean> getPermissionMap(PlaylistRole contentGroupRole) {
        return ROLE_PLAYLIST_PERMISSIONS.get(contentGroupRole);
    }

    @Override
    protected List<PlaylistAction> getActions() {
        return PLAYLIST_ACTIONS;
    }
}
