package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.UserManagedGroup;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserManagedGroupMapper extends BaseMapper<UserManagedGroup> {
    List<UserManagedGroup> findUserManagedGroups(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId);
}
