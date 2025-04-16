package com.threeatom.guidecore.service;

import com.threeatom.system.entity.SysFile;

public interface VideoThumbnailProvider {

    String getThumbnailUrl(SysFile sysFile);
}
