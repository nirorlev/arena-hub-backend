package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubjectComplete;
import java.util.List;

/**
 * @author PC
 * @title: GcSubjectCompleteMapper
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/811:18
 */
public interface GcSubjectCompleteMapper extends BaseMapper<GcSubjectComplete> {

    GcSubjectComplete getSubjectCompleteInfo(Integer masterId, Integer userId, Integer subjectId);

    void updateStateByVideoId(Integer masterId, Integer videoId);

    List<GcSubjectComplete> selectBySubjectId(Integer masterId, Integer subjectId);
}
