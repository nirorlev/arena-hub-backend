package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUserSaveContentFollow;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GcUserSaveContentFollowMapper extends BaseMapper<GcUserSaveContentFollow> {

    List<Integer> selectFollowList(
            @Param("userId") Integer userId, @Param("masterId") Integer masterId);

    List<GcUserSaveContentFollow> selectFollowListByPlayListId(@Param("ids") List<Integer> ids);
}
