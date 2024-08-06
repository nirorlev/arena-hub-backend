package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PtTags;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PtTagsMapper extends BaseMapper<PtTags> {

    List<String> selectPtTagList(@Param("ptTags") PtTags ptTags);
}
