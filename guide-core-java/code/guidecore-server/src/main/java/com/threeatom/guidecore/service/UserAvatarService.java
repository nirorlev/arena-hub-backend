package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.UserAvatar;

public interface UserAvatarService extends IService<UserAvatar> {
    UserAvatar saveUserAvatar(String powtoonFileUrl, Integer userId, Integer masterId);

    void updateUserAvatar(String powtoonProfileUrl, Integer userId, Integer masterId);
}
