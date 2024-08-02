package com.threeatom.guidecore.service;

import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.entity.GcUserSaveContent;
import com.threeatom.guidecore.entity.GcVideoComment;
import com.threeatom.system.entity.SysFile;
import java.util.List;

public interface UnavailableVideoService {
    void nullifyVideoData(List<SysFile> files);

    void nullifyPlaylistContent(List<GcUserSaveContent> playlistContent);

    void nullifyVideoData(SysFile file);

    void nullifyVideoComments(PageInfo<GcVideoComment> comments, Integer videoId);
}
