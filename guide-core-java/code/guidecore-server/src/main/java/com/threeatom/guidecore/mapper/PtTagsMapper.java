package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.PtTags;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 * @title: PtTagsMapper
 * @projectName
 * @description: TODO
 * @date 2023/1/9/00910:32
 */
public interface PtTagsMapper extends BaseMapper<PtTags> {

    List<String> selectPtTagList(@Param("ptTags") PtTags ptTags);
}
