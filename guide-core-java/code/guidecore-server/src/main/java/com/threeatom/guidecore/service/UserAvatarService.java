package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.dto.response.ContentGroupChannelSubscriptionDto;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.PtChannel;
import com.threeatom.guidecore.entity.UserAvatar;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

public interface UserAvatarService extends IService<UserAvatar> {
    UserAvatar saveUserAvatar(String powtoonFileUrl, Integer userId, Integer masterId);

    void updateUserAvatar(String powtoonProfileUrl, Integer userId, Integer masterId);
}
