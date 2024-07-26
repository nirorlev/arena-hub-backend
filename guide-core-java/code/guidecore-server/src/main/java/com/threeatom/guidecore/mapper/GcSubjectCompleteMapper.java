package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubjectComplete;
import java.util.List;

public interface GcSubjectCompleteMapper extends BaseMapper<GcSubjectComplete> {

    GcSubjectComplete getSubjectCompleteInfo(Integer masterId, Integer userId, Integer subjectId);

    void updateStateByVideoId(Integer masterId, Integer videoId);

    List<GcSubjectComplete> selectBySubjectId(Integer masterId, Integer subjectId);
}
