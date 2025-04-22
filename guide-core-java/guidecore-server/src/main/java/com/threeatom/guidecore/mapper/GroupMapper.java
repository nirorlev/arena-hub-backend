package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.Group;
import java.util.List;

public interface GroupMapper extends BaseMapper<Group> {
    List<Group> findGroups(Integer userId, Integer masterId);
}
