package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.PortalUser;

public interface PortalUserService extends IService<PortalUser> {
    void saveOrUpdate(Integer userId, Integer masterId, String role);

    PortalUser getByUserAndMasterId(Integer userId, Integer masterId);
}
