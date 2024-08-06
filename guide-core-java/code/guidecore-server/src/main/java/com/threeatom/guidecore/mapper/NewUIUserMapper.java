package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserMessage;
import java.util.List;

public interface NewUIUserMapper extends BaseMapper<GcUser> {

    List<GcUserMessage> countNewMessagesDetail(
            Integer uid, Integer portalId, Integer teacherAccessId);
}
