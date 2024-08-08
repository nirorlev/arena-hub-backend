package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.UserLicense;
import com.threeatom.guidecore.mapper.UserLicenseMapper;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.UserLicenseService;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserLicenseServiceImpl extends ServiceImpl<UserLicenseMapper, UserLicense>
    implements UserLicenseService {

    private static final String LIMITED_MEMBER = "limitedMember";

    private final PtChannelService channelService;
    private final GcUserSaveFolderService playlistService;
    private final OrgLicenseLimitService orgLicenseLimitService;

    @Override
    public void update(Integer userId, PowtoonUserDto powtoonUserDto, Integer masterId) {
        getByUserId(userId).ifPresentOrElse(
            userLicense -> updateExisting(userLicense, powtoonUserDto, masterId),
            () -> create(powtoonUserDto, masterId, userId));
    }

    @Override
    public void addPlaylistCount(GcUserSaveFolder gcUserSaveFolder, Integer userId) {
        UserLicense dbUserLicense = getByUserId(userId).orElseThrow();

        if (gcUserSaveFolder.getIsPrivate()) {
            updateCount(
                userLicense -> userLicense.setPrivatePlaylistCount(userLicense.getPrivatePlaylistCount() + 1),
                dbUserLicense);
            return;
        }

        orgLicenseLimitService.checkPlaylistLimit(dbUserLicense.getOrgLicenseId(), dbUserLicense.getPublishPlaylistCount() + 1);
        updateCount(
            userLicense -> userLicense.setPublishPlaylistCount(userLicense.getPublishPlaylistCount() + 1),
            dbUserLicense);
    }

    @Override
    public void decreasePlaylistCount(Integer userId, Integer playlistId) {
        GcUserSaveFolder gcUserSaveFolder = playlistService.getById(playlistId);
        if (gcUserSaveFolder == null) {
            log.info("Failed to decrease playlist. Playlist with id {} not found", playlistId);
            return;
        }

        if (gcUserSaveFolder.getIsPrivate()) {
            updateCount(
                userLicense -> userLicense.setPrivatePlaylistCount(userLicense.getPrivatePlaylistCount() - 1),
                getByUserId(userId).orElseThrow());
            return;
        }

        updateCount(
            userLicense -> userLicense.setPublishPlaylistCount(userLicense.getPublishPlaylistCount() - 1),
            getByUserId(userId).orElseThrow());
    }

    @Override
    public void decreaseChannelCount(Integer userId, Integer channelId) {
        PtChannel channel = channelService.getById(channelId);
        if (channel == null) {
            log.info("Failed to decrease channel. Channel with id {} not found", channelId);
            return;
        }

        if (channel.getFid() != null) {
            return;
        }

        if (channel.getIsPrivate()) {
            updateCount(
                userLicense -> userLicense.setPrivateChannelCount(userLicense.getPrivateChannelCount() - 1),
                getByUserId(userId).orElseThrow());
            return;
        }

        updateCount(
            userLicense -> userLicense.setPublishChannelCount(userLicense.getPublishChannelCount() - 1),
            getByUserId(userId).orElseThrow());
    }

    @Override
    public void addChannelCount(PtChannel ptChannel, Integer userId) {
        if (ptChannel.getId() != null) {
            return;
        }

        UserLicense dbUserLicense = getByUserId(userId).orElseThrow();
        if (ptChannel.getIsPrivate()) {
            updateCount(
                userLicense -> userLicense.setPrivateChannelCount(userLicense.getPrivateChannelCount() + 1),
                dbUserLicense);
            return;
        }

        orgLicenseLimitService.checkChannelLimit(dbUserLicense.getOrgLicenseId(), dbUserLicense.getPublishChannelCount() + 1);
        updateCount(
            userLicense -> userLicense.setPublishChannelCount(userLicense.getPublishChannelCount() + 1),
            dbUserLicense);
    }

    private void updateCount(Consumer<UserLicense> updateCountSupplier, UserLicense userLicense) {
        updateCountSupplier.accept(userLicense);
        this.updateById(userLicense);
    }

    public void setActive(Integer userId, boolean active) {
        UserLicense userLicense = getByUserId(userId).orElseThrow();
        userLicense.setActive(active);
        this.updateById(userLicense);
    }

    private void create(PowtoonUserDto powtoonUserDto, Integer masterId, Integer userId) {
        UserLicense userLicense = createNew(powtoonUserDto, masterId, userId);
        this.save(userLicense);
    }

    private UserLicense createNew(PowtoonUserDto powtoonUserDto, Integer masterId, Integer userId) {
        UserLicense userLicense = createNewLicense(userId, masterId);
        updateLimits(userLicense, shouldLicenseBeActive(powtoonUserDto), masterId);
        return userLicense;
    }

    private UserLicense createNewLicense(Integer userId, Integer masterId) {
        UserLicense userLicense = new UserLicense();

        userLicense.setUserId(userId);
        OrgLicenseLimit defaultLicenseLimit = orgLicenseLimitService.getDefaultLicenseLimit(masterId);
        userLicense.setOrgLicenseId(defaultLicenseLimit.getId());

        return userLicense;
    }

    private boolean shouldLicenseBeActive(PowtoonUserDto powtoonUserDto) {
        return LIMITED_MEMBER.equals(powtoonUserDto.getPermissions().getOrg().getRoleId());
    }

    private void updateLimits(UserLicense userLicense, boolean active, Integer masterId) {
        Integer userId = userLicense.getUserId();

        userLicense.setPrivateChannelCount(channelService.countUserPrivateChannels(userId, masterId));
        userLicense.setPrivatePlaylistCount(playlistService.countUserPrivatePlaylists(userId, masterId));

        userLicense.setPublishChannelCount(channelService.countUserPublicChannels(userId, masterId));
        userLicense.setPublishPlaylistCount(playlistService.countUserPublicPlaylists(userId, masterId));
        setActive(userId, active);
    }

    private void updateExisting(UserLicense userLicense, PowtoonUserDto powtoonUserDto, Integer masterId) {
        if (shouldLicenseBeActive(powtoonUserDto)) {
            updateLimits(userLicense, true, masterId);
            return;
        }

        setActive(userLicense.getUserId(), false);
    }

    @Transactional(readOnly = true)
    public Optional<UserLicense> getByUserId(Integer userId) {
        QueryWrapper<UserLicense> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Optional.ofNullable(this.getOne(queryWrapper));
    }
}
