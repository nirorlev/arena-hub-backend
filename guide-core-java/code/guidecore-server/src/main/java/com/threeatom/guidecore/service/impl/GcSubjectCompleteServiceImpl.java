package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcSubjectCompleteMapper;
import com.threeatom.guidecore.service.*;
import java.util.*;
import org.springframework.stereotype.Service;

/**
 * @author PC
 * @title: GcSubjectCompleteServiceImpl
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/811:17
 */
@Service
public class GcSubjectCompleteServiceImpl
        extends ServiceImpl<GcSubjectCompleteMapper, GcSubjectComplete>
        implements GcSubjectCompleteService {

    @Override
    public GcSubjectComplete getSubjectCompleteInfo(
            Integer masterId, Integer userId, Integer subjectId) {
        return this.baseMapper.getSubjectCompleteInfo(masterId, userId, subjectId);
    }

    @Override
    public void updateStateByVideoId(Integer masterId, Integer videoId) {
        this.baseMapper.updateStateByVideoId(masterId, videoId);
    }

    @Override
    public List<GcSubjectComplete> selectBySubjectId(Integer masterId, Integer subjectId) {
        return this.baseMapper.selectBySubjectId(masterId, subjectId);
    }
}
