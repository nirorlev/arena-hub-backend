package com.threeatom.guidecore.service.impl;

import com.github.pagehelper.PageInfo;
import com.threeatom.common.permissions.service.AuthorizationService;
import com.threeatom.guidecore.constant.PermitAction;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.UnavailableVideoService;
import com.threeatom.system.entity.SysFile;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnavailableVideoServiceImpl implements UnavailableVideoService {

    private static final String FEATURE_NAME = "unavailableVideoRandomEnabled";

    private final FeatureToggleService featureToggleService;
    private final AuthorizationService authorizationService;

    private List<Consumer<SysFile>> videoFileNullifySuppliers;
    private List<Consumer<GcVideo>> videoNullifySuppliers;
    private List<Consumer<GcUserSaveContent>> playlistContentNullifySuppliers;

    @PostConstruct
    public void init() {
        initVideoFileNullifySuppliers();
        initVideoNullifySuppliers();
        initPlaylistContentNullifySuppliers();
    }

    private void initVideoFileNullifySuppliers() {
        videoFileNullifySuppliers = List.of(
            file -> file.setDescription(null),
            file -> file.setFileUrl(null),
            file -> file.setFolder(null),
            file -> file.setId(null),
            file -> file.setName(null),
            file -> file.setVideoLong(0),
            file -> file.setFullFileUrl(null),
            file -> file.setSnapshotUrl(null),
            file -> file.setFileTypeIndex(null)
        );
    }

    private void initVideoNullifySuppliers() {
        videoNullifySuppliers = List.of(
            video -> video.setVideoDesc(null),
            video -> video.setSourceUrl(null),
            video -> video.setVideoFullUrl(null),
            video -> video.setFileTypeIndex(null),
            video -> video.setSnapshotUrl(null),
            video -> video.setThumbnailUrl(null)
        );
    }

    private void initPlaylistContentNullifySuppliers() {
        playlistContentNullifySuppliers = List.of(
            playlistContent -> playlistContent.setFileId(null)
        );
    }

    @Override
    public void nullifyVideoData(PortalUser portalUser, List<GcVideo> videos) {
        videos.stream()
            .filter(video -> isVideoUnavailable(portalUser, video))
            .forEach(video -> {
                videoFileNullifySuppliers.forEach(supplier -> supplier.accept(video.getVideoFile()));
                videoNullifySuppliers.forEach(supplier -> supplier.accept(video));
            });
    }

    private boolean featureIsEnabled() {
        return Boolean.parseBoolean(featureToggleService.getFeatureToggle(FEATURE_NAME).getValue());
    }

    @Override
    public void nullifyPlaylistContent(PortalUser portalUser, List<GcUserSaveContent> playlistContent) {
        playlistContent.stream()
            .filter(content -> isVideoUnavailable(portalUser, content.getVideo()))
            .forEach(content -> {
                playlistContentNullifySuppliers.forEach(supplier -> supplier.accept(content));
                videoFileNullifySuppliers.forEach(supplier -> supplier.accept(content.getVideoFile()));
            });
    }

    @Override
    public void nullifyVideoData(PortalUser portalUser, GcVideo video) {
        nullifyVideoData(portalUser, List.of(video));
    }

    @Override
    public void nullifyVideoComments(PortalUser portalUser, GcVideo video, PageInfo<GcVideoComment> comments) {
        if (!isVideoUnavailable(portalUser, video)) {
            return;
        }

        comments.getList().clear();
    }

    private boolean isVideoUnavailable(PortalUser portalUser, GcVideo video) {
        boolean isViewAllowed = authorizationService.checkAccess(video, PermitAction.VIEW, portalUser);
        return featureIsEnabled() && !isViewAllowed;
    }
}
