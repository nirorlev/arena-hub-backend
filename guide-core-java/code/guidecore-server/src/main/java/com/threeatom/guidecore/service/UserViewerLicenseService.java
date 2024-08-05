package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.client.dto.PowtoonUserDto;
import com.threeatom.guidecore.entity.UserViewerLicense;

public interface UserViewerLicenseService extends IService<UserViewerLicense> {
    void update(Integer userId, PowtoonUserDto powtoonUserDto, Integer masterId);
}
