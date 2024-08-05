package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.guidecore.entity.UserViewerLicense;
import com.threeatom.guidecore.mapper.UserViewerLicenseMapper;
import com.threeatom.guidecore.service.GcUserSaveFolderService;
import com.threeatom.guidecore.service.PtChannelService;
import com.threeatom.guidecore.service.UserViewerLicenseService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserViewerLicenseServiceImpl extends ServiceImpl<UserViewerLicenseMapper, UserViewerLicense>
    implements UserViewerLicenseService {

    private static final String LIMITED_MEMBER = "limitedMember";

    private final PtChannelService channelService;
    private final GcUserSaveFolderService playlistService;

    @Value("${publish.channel.limit.default}")
    private int publishChannelLimit;
    @Value("${publish.playlist.limit.default}")
    private int publishPlaylistLimit;

    @Override
    public void update(Integer userId, PowtoonUserDto powtoonUserDto, Integer masterId) {
        getByUserId(userId).ifPresentOrElse(
            userViewerLicense -> updateExisting(userViewerLicense, powtoonUserDto, masterId),
            () -> create(powtoonUserDto, masterId, userId));
    }

    public void setActive(Integer userId, boolean active) {
        UserViewerLicense userViewerLicense = getByUserId(userId).orElseThrow();
        userViewerLicense.setActive(active);
        this.updateById(userViewerLicense);
    }

    private void create(PowtoonUserDto powtoonUserDto, Integer masterId, Integer userId) {
        UserViewerLicense userViewerLicense = createNew(powtoonUserDto, masterId, userId);
        this.save(userViewerLicense);
    }

    private UserViewerLicense createNew(PowtoonUserDto powtoonUserDto, Integer masterId, Integer userId) {
        UserViewerLicense userViewerLicense = createNewLicense(userId);

        updateLimits(userViewerLicense, shouldLicenseBeActive(powtoonUserDto), masterId);
        return userViewerLicense;
    }

    private UserViewerLicense createNewLicense(Integer userId) {
        UserViewerLicense userViewerLicense = new UserViewerLicense();

        userViewerLicense.setUserId(userId);
        userViewerLicense.setPublishChannelLimit(publishChannelLimit);
        userViewerLicense.setPublishPlaylistLimit(publishPlaylistLimit);
        return userViewerLicense;
    }

    private boolean shouldLicenseBeActive(PowtoonUserDto powtoonUserDto) {
        return LIMITED_MEMBER.equals(powtoonUserDto.getPermissions().getOrg().getRoleId());
    }

    private void updateLimits(UserViewerLicense userViewerLicense, boolean active, Integer masterId) {
        Integer userId = userViewerLicense.getUserId();

        userViewerLicense.setPrivateChannelCount(channelService.countUserPrivateChannels(userId, masterId));
        userViewerLicense.setPrivatePlaylistCount(playlistService.countUserPrivatePlaylists(userId, masterId));

        userViewerLicense.setPublishChannelCount(channelService.countUserPublicChannels(userId, masterId));
        userViewerLicense.setPublishPlaylistCount(playlistService.countUserPublicPlaylists(userId, masterId));
        setActive(userId, active);
    }

    private void updateExisting(UserViewerLicense userViewerLicense, PowtoonUserDto powtoonUserDto, Integer masterId) {
        if (shouldLicenseBeActive(powtoonUserDto)) {
            updateLimits(userViewerLicense, true, masterId);
            return;
        }

        setActive(userViewerLicense.getUserId(), false);
    }

    @Transactional(readOnly = true)
    public Optional<UserViewerLicense> getByUserId(Integer userId) {
        QueryWrapper<UserViewerLicense> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return Optional.ofNullable(this.getOne(queryWrapper));
    }
}
