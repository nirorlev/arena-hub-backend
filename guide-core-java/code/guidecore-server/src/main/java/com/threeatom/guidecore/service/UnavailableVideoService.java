package com.threeatom.guidecore.service;

import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.system.entity.SysFile;
import java.util.List;

public interface UnavailableVideoService {
    void nullifyVideoData(PortalUser portalUser, List<GcVideo> videos, List<SysFile> videoFiles);

    void nullifyPlaylistContent(PortalUser portalUser, List<GcUserSaveContent> playlistContent);

    void nullifyVideoData(PortalUser portalUser, GcVideo video);

    void nullifyVideoComments(PortalUser portalUser, GcVideo video, PageInfo<GcVideoComment> comments);
}
