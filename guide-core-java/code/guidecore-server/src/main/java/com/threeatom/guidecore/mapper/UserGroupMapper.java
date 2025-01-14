package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GroupToUserPk;
import com.threeatom.guidecore.entity.UserGroup;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserGroupMapper extends BaseMapper<UserGroup> {

    UserGroup getById(@Param("id") GroupToUserPk id);

    void saveOrUpdateBatch(@Param("userGroups") Collection<UserGroup> userGroups);

    List<UserGroup> findByUserAndMasterId(@Param("userId") Integer userId, @Param("masterId") Integer masterId);

    boolean removeByIds(@Param("ids") Collection<? extends Serializable> ids);
}
