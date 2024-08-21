package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.guidecore.dto.response.LicenseUsageDto;
import com.threeatom.guidecore.entity.GcUserSaveFolder;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.UserLicense;

public interface UserLicenseService extends IService<UserLicense> {
    void update(Integer userId, PowtoonUserDto powtoonUserDto, Integer masterId);

    void addPlaylistCount(GcUserSaveFolder gcUserSaveFolder, Integer userId);

    void decreasePlaylistCount(Integer userId, Integer folderId);

    void decreaseChannelCount(Integer userId, Integer userId1);

    void addChannelCount(PtChannel ptChannel, Integer userId);

    LicenseUsageDto getPermissions(Integer userId);
}
