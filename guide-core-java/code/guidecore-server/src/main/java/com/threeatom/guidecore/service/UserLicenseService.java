package com.threeatom.guidecore.service;

import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;

public interface UserLicenseService {

    void checkPlaylistLimit(GcUserSaveFolder playlist, PortalUser portalUser);

    void checkChannelLimit(PtChannel channel, PortalUser portalUser);

    LicenseUsageDto getLicenseUsage(PortalUser portalUser);
}
