package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PtChannelContent;
import com.threeatom.system.entity.SysFile;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PtchannelContentMapper extends BaseMapper<PtChannelContent> {

    List<SysFile> selectVideosInChannel(
            @Param("channelId") Integer channelId,
            @Param("order") String order,
            @Param("fileId") Integer fileId);
}
