package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubjectComplete;
import java.util.List;

public interface GcSubjectCompleteService extends IService<GcSubjectComplete> {

    GcSubjectComplete getSubjectCompleteInfo(Integer masterId, Integer userId, Integer subjectId);

    void updateStateByVideoId(Integer masterId, Integer videoId);

    List<GcSubjectComplete> selectBySubjectId(Integer masterId, Integer subjectId);
}
