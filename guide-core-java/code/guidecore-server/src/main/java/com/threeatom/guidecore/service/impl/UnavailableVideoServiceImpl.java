package com.threeatom.guidecore.service.impl;

import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.service.UnavailableVideoService;
import com.threeatom.system.entity.SysFile;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class UnavailableVideoServiceImpl implements UnavailableVideoService {

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
    public void nullifyVideoData(List<SysFile> files) {
        files.stream()
            .filter(this::isVideoUnavailable)
            .forEach(file -> {
                videoFileNullifySuppliers.forEach(supplier -> supplier.accept(file));
            });
    }

    @Override
    public void nullifyPlaylistContent(List<GcUserSaveContent> playlistContent) {
        playlistContent.stream()
            .filter(content -> isVideoUnavailable(content.getVideoFile()))
            .forEach(content -> {
                playlistContentNullifySuppliers.forEach(supplier -> supplier.accept(content));
                videoFileNullifySuppliers.forEach(supplier -> supplier.accept(content.getVideoFile()));
            });
    }


    @Override
    public void nullifyVideoData(SysFile file) {
        nullifyVideoData(Collections.singletonList(file));
    }

    @Override
    public void nullifyVideoComments(PageInfo<GcVideoComment> comments, Integer videoId) {
        comments.getList().clear();
    }

    private boolean isVideoUnavailable(SysFile videoFile) {
        return Math.random() > 0.8;
    }
}
