package com.threeatom.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysFileCaption;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysFileCaptionMapper extends BaseMapper<SysFileCaption> {
    SysFile selectFileCaption(@Param("id") Integer id);

    List<SysFileCaption> selectSysFileCaptionId(@Param("id") Integer id);

    Integer saveYmTaskId(@Param("id") Integer id);

    SysFileCaption selectById(@Param("id") Integer id);

    SysFileCaption selectMainSysFile(@Param("id") Integer id);

    SysFileCaption getCaptionInfo(@Param("caption") SysFileCaption sysFileCaption);

    void updateCaptionState(@Param("list") List<String> list, @Param("videoId") Integer videoId);

    void updateInCaptionState(@Param("list") List<String> list, @Param("videoId") Integer videoId);

    SysFileCaption selectSrtData(@Param("videoId") Integer videoId);
}
