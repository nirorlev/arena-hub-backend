package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PortalUser;
import org.apache.ibatis.annotations.Param;

public interface PortalUserMapper extends BaseMapper<PortalUser> {
    PortalUser getByUserAndMasterId(@Param("userId") Integer userId, @Param("masterId") Integer masterId);
}
