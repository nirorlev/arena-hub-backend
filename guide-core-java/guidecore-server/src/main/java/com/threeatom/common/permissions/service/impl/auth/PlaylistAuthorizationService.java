package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitCollection;
import com.threeatom.common.permissions.dto.PermitPlaylist;
import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.enums.PlaylistAction;
import com.threeatom.common.permissions.enums.PlaylistRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
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
    private static final Map<PlaylistRole, Map<PlaylistAction, Predicate<PermitPlaylist>>> ROLE_PLAYLIST_PERMISSIONS =
        Map.of(
            PlaylistRole.VIEWER, Map.of(
                PlaylistAction.CREATE, playlist -> false,
                PlaylistAction.DELETE, playlist -> false,
                PlaylistAction.VIEW, playlist -> true,
                PlaylistAction.EDIT, playlist -> false,
                PlaylistAction.SHARE, playlist -> true,
                PlaylistAction.SUBSCRIBE, playlist -> true,
                PlaylistAction.UNSUBSCRIBE, playlist -> true,
                PlaylistAction.ADD_CONTENT, playlist -> false,
                PlaylistAction.MANAGE_CONTENT, playlist -> false,
                PlaylistAction.PUBLISH, playlist -> false
            ),
            PlaylistRole.ADMIN, Map.of(
                PlaylistAction.CREATE, playlist -> true,
                PlaylistAction.DELETE, playlist -> true,
                PlaylistAction.VIEW, playlist -> true,
                PlaylistAction.EDIT, playlist -> true,
                PlaylistAction.SHARE, PermitCollection::isPublic,
                PlaylistAction.SUBSCRIBE, playlist -> true,
                PlaylistAction.UNSUBSCRIBE, playlist -> true,
                PlaylistAction.ADD_CONTENT, playlist -> true,
                PlaylistAction.MANAGE_CONTENT, playlist -> true,
                PlaylistAction.PUBLISH, playlist -> true
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
    protected Map<PlaylistAction, Predicate<PermitPlaylist>> getPermissionMap(PlaylistRole contentGroupRole) {
        return ROLE_PLAYLIST_PERMISSIONS.get(contentGroupRole);
    }

    @Override
    protected List<PlaylistAction> getActions() {
        return PLAYLIST_ACTIONS;
    }
}
