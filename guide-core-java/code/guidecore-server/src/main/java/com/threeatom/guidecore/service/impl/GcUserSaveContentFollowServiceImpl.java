package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcUserSaveContentFollow;
import com.threeatom.guidecore.mapper.GcUserSaveContentFollowMapper;
import com.threeatom.guidecore.service.GcUserSaveContentFollowService;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-11
 */
@Service
public class GcUserSaveContentFollowServiceImpl
        extends ServiceImpl<GcUserSaveContentFollowMapper, GcUserSaveContentFollow>
        implements GcUserSaveContentFollowService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GcUserSaveContentFollowServiceImpl.class);

    @Resource GcUserSaveContentFollowMapper gcUserSaveContentFollowMapper;

    @Override
    public List<Integer> selectFollowPlayList(Integer userId, Integer masterId) {
        return gcUserSaveContentFollowMapper.selectFollowList(userId, masterId);
    }

    @Override
    public GcUserSaveContentFollow selectFollowByUserId(Integer userId, Integer folderId) {
        QueryWrapper<GcUserSaveContentFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId);
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<GcUserSaveContentFollow> selectFollowListByPlayListId(List<Integer> ids) {
        return gcUserSaveContentFollowMapper.selectFollowListByPlayListId(ids);
    }
}
