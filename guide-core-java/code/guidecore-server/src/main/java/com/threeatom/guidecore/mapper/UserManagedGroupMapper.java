package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GroupToUserPk;
import com.threeatom.guidecore.entity.UserManagedGroup;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserManagedGroupMapper extends BaseMapper<UserManagedGroup> {
    List<UserManagedGroup> findUserManagedGroups(
        @Param("userId") Integer userId,
        @Param("masterId") Integer masterId);

    UserManagedGroup getById(@Param("id") GroupToUserPk id);

    void saveOrUpdateBatch(@Param("userManagedGroups") Collection<UserManagedGroup> userManagedGroups);

    boolean removeByIds(@Param("ids") Collection<? extends Serializable> ids);
}
