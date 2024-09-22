package com.threeatom.common.permissions.service.impl.auth;

import com.threeatom.common.permissions.dto.PermitUser;
import com.threeatom.common.permissions.dto.PermitVideoItem;
import com.threeatom.common.permissions.enums.VideoItemAction;
import com.threeatom.common.permissions.enums.VideoItemRole;
import com.threeatom.common.permissions.service.ResourceAuthorizationService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class VideoItemAuthorizationService
    extends ResourceAuthorizationService<PermitVideoItem, VideoItemRole, VideoItemAction> {

    private static final List<VideoItemAction> VIDEO_ITEM_ACTIONS = List.of(
        VideoItemAction.CREATE,
        VideoItemAction.DELETE,
        VideoItemAction.VIEW,
        VideoItemAction.EDIT,
        VideoItemAction.SHARE,
        VideoItemAction.COMMENT
    );
    private static final Map<VideoItemRole, Map<VideoItemAction, Boolean>> ROLE_VIDEO_ITEM_PERMISSIONS = Map.of(
        VideoItemRole.VIEWER, Map.of(
            VideoItemAction.CREATE, false,
            VideoItemAction.DELETE, false,
            VideoItemAction.VIEW, true,
            VideoItemAction.EDIT, false,
            VideoItemAction.SHARE, true,
            VideoItemAction.COMMENT, true
        ),
        VideoItemRole.ADMIN, Map.of(
            VideoItemAction.CREATE, true,
            VideoItemAction.DELETE, true,
            VideoItemAction.VIEW, true,
            VideoItemAction.EDIT, true,
            VideoItemAction.SHARE, true,
            VideoItemAction.COMMENT, true
        )
    );

    @Override
    public VideoItemRole getRole(PermitUser permitUser, PermitVideoItem videoItem) {
        if (permitUser.getId().equals(videoItem.getOwnerId())
            || permitUser.isOrgAdmin() && !videoItem.isPrivate()) {
            return VideoItemRole.ADMIN;
        }

        if (videoItem.isPublic() || userHasVideoItemInContentGroups(permitUser, videoItem)) {
            return VideoItemRole.VIEWER;
        }

        return null;
    }

    private boolean userHasVideoItemInContentGroups(PermitUser permitUser, PermitVideoItem videoItem) {
        Set<String> allUserContentGroupIds = permitUser.getContentGroupIds();
        allUserContentGroupIds.addAll(permitUser.getManagedContentGroupIds());

        return allUserContentGroupIds.stream()
            .anyMatch(userContentGroupId -> videoItem.getContentGroupIds().contains(userContentGroupId));
    }

    @Override
    protected Map<VideoItemAction, Boolean> getPermissionMap(VideoItemRole videoItemRole) {
        return ROLE_VIDEO_ITEM_PERMISSIONS.get(videoItemRole);
    }

    @Override
    protected List<VideoItemAction> getActions() {
        return VIDEO_ITEM_ACTIONS;
    }
}
