package com.threeatom.guidecore.service.impl;

import com.github.pagehelper.PageInfo;
import com.threeatom.common.permit.service.PermitService;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.service.FeatureToggleService;
import com.threeatom.guidecore.service.UnavailableVideoService;
import com.threeatom.system.entity.SysFile;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnavailableVideoServiceImpl implements UnavailableVideoService {

    private static final String FEATURE_NAME = "unavailableVideoRandomEnabled";

    private final FeatureToggleService featureToggleService;
    private final PermitService permitService;

    private List<Consumer<SysFile>> videoFileNullifySuppliers;
    private List<Consumer<GcUserSaveContent>> playlistContentNullifySuppliers;

    @PostConstruct
    public void init() {
        initVideoFileNullifySuppliers();
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
            file -> file.setSnapshotUrl(null)
        );
    }

    private void initPlaylistContentNullifySuppliers() {
        playlistContentNullifySuppliers = List.of(
            playlistContent -> playlistContent.setFileId(null)
        );
    }

    @Override
    public void nullifyVideoData(PortalUser portalUser, List<GcVideo> videos, List<SysFile> files) {
        Map<Integer, SysFile> videoIdToVideoFile = files.stream()
            .collect(Collectors.toMap(SysFile::getVideoId, Function.identity()));

        videos.stream()
            .filter(video -> isVideoUnavailable(portalUser, video))
            .forEach(video -> {
                videoFileNullifySuppliers.forEach(supplier -> supplier.accept(videoIdToVideoFile.get(video.getId())));
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
        nullifyVideoData(portalUser, List.of(video), List.of(video.getVideoFile()));
    }

    @Override
    public void nullifyVideoComments(PortalUser portalUser, GcVideo video, PageInfo<GcVideoComment> comments) {
        comments.getList().clear();
    }

    private boolean isVideoUnavailable(PortalUser portalUser, GcVideo video) {
        boolean isViewAllowed = permitService.checkPermit(video, "view", portalUser);
        return featureIsEnabled() && !isViewAllowed;
    }
}
