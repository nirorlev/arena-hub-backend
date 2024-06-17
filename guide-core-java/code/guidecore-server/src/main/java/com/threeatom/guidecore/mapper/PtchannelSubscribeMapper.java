package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PtChannelSubscribe;
import org.apache.ibatis.annotations.Param;

public interface PtchannelSubscribeMapper extends BaseMapper<PtChannelSubscribe> {
    void updateIsDeleted(@Param("id") Integer id, @Param("isDeleted") boolean isDeleted);
}
