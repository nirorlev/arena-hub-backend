package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserSaveContentFollow;
import java.util.List;

public interface GcUserSaveContentFollowService extends IService<GcUserSaveContentFollow> {

    List<Integer> selectFollowPlayList(Integer userId, Integer masterId);

    GcUserSaveContentFollow selectFollowByUserId(Integer userId, Integer folderId);

    List<GcUserSaveContentFollow> selectFollowListByPlayListId(List<Integer> ids);
}
