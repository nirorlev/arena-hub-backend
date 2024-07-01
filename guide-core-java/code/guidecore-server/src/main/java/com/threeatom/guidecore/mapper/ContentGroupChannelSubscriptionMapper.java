package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.ContentGroupChannelSubscription;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContentGroupChannelSubscriptionMapper
    extends BaseMapper<ContentGroupChannelSubscription> {

    List<ContentGroupChannelSubscription> findByContentGroupId(
        @Param("contentGroupId") Integer contentGroupId,
        @Param("isSubscribed") Boolean isSubscribed);

    List<ContentGroupChannelSubscription> getSubscribedChannelIdsExceptOwned(
        @Param("contentGroupIds") List<Integer> contentGroupIds,
        @Param("ownerId") Integer ownerId);
}
