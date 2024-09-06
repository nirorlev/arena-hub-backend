package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserInfo;

public interface GcUserInfoService extends IService<GcUserInfo> {
    GcUserInfo createNew(String firstName, String lastName);
}
