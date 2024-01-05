package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.threeatom.guidecore.constant.EnvType;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.*;
import com.threeatom.guidecore.mapper.GcSubjectCompleteMapper;
import com.threeatom.guidecore.service.*;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author PC
 * @title: GcSubjectCompleteServiceImpl
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/811:17
 */
@Service
public class GcSubjectCompleteServiceImpl extends ServiceImpl<GcSubjectCompleteMapper, GcSubjectComplete> implements GcSubjectCompleteService {



    @Override
    public GcSubjectComplete getSubjectCompleteInfo(Integer masterId, Integer userId, Integer subjectId) {
        return this.baseMapper.getSubjectCompleteInfo(masterId,userId,subjectId);
    }

    @Override
    public void updateStateByVideoId(Integer masterId, Integer videoId) {
        this.baseMapper.updateStateByVideoId(masterId,videoId);
    }

    @Override
    public List<GcSubjectComplete> selectBySubjectId(Integer masterId, Integer subjectId) {
        return this.baseMapper.selectBySubjectId(masterId,subjectId);
    }
}
