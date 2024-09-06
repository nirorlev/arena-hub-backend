package com.threeatom.guidecore.service.impl;

import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.enums.UserOrgRole;
import com.threeatom.guidecore.exception.LicenseLimitExceededException;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.UserLicenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLicenseServiceImpl implements UserLicenseService {

    private final PtChannelService channelService;
    private final GcUserSaveFolderService playlistService;
    private final OrgLicenseLimitService orgLicenseLimitService;

    @Override
    public void checkPlaylistLimit(GcUserSaveFolder playlist, PortalUser portalUser) {
        if (shouldLicenseBeActive(portalUser.getRole()) || playlist.getIsPrivate()) {
            return;
        }

        Integer publicPlaylistsCount =
            playlistService.countUserPublicPlaylists(portalUser.getUserId(), portalUser.getMasterId());
        OrgLicenseLimit orgLicenseLimit = orgLicenseLimitService.getByMasterId(portalUser.getMasterId());
        Integer publishedPlaylistLimit = orgLicenseLimit.getPublishedPlaylistLimit();

        if (publicPlaylistsCount + 1 >= publishedPlaylistLimit) {
            log.info("User with id {} has reached the limit of public playlists ({})", portalUser.getUserId(),
                publishedPlaylistLimit);
            throw new LicenseLimitExceededException("User has reached the limit of public playlists");
        }
    }

    @Override
    public void checkChannelLimit(PtChannel channel, PortalUser portalUser) {
        if (shouldLicenseBeActive(portalUser.getRole()) || channel.getIsPrivate() || channel.getFid() != null) {
            return;
        }

        Integer publicChannelCount =
            channelService.countUserPublicChannels(portalUser.getUserId(), portalUser.getMasterId());
        OrgLicenseLimit orgLicenseLimit = orgLicenseLimitService.getByMasterId(portalUser.getMasterId());
        Integer publishedChannelLimit = orgLicenseLimit.getPublishedChannelLimit();

        if (publicChannelCount + 1 >= publishedChannelLimit) {
            log.info("User with id {} has reached the limit of public channels ({})", portalUser.getUserId(),
                publishedChannelLimit);
            throw new LicenseLimitExceededException("User has reached the limit of public channels");
        }
    }

    @Override
    public LicenseUsageDto getLicenseUsage(Integer userId, Integer masterId) {
        LicenseUsageDto licenseUsage = new LicenseUsageDto();

        licenseUsage.setPrivateChannelCount(channelService.countUserPrivateChannels(userId, masterId));
        licenseUsage.setPrivatePlaylistCount(playlistService.countUserPrivatePlaylists(userId, masterId));

        licenseUsage.setPublishedChannelCount(channelService.countUserPublicChannels(userId, masterId));
        licenseUsage.setPublishedPlaylistCount(playlistService.countUserPublicPlaylists(userId, masterId));

        return licenseUsage;
    }

    private boolean shouldLicenseBeActive(UserOrgRole role) {
        return UserOrgRole.LIMITED_MEMBER.equals(role);
    }
}
