package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContentGroupChannelSubscriptionMapper extends BaseMapper<ContentGroupChannelSubscription> {

    List<ContentGroupChannelSubscription> findByContentGroupId(
        @Param("contentGroupId") Integer contentGroupId,
        @Param("autoSubscribe") Boolean autoSubscribe);

    ContentGroupChannelSubscription getById(@Param("id") Integer id);
}
