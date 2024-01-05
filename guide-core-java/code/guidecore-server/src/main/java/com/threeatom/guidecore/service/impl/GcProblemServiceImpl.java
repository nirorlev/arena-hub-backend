package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcFaq;
import com.threeatom.guidecore.entity.GcKnowledgebase;
import com.threeatom.guidecore.entity.GcProblem;
import com.threeatom.guidecore.mapper.GcProblemMapper;
import com.threeatom.guidecore.service.GcProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GcProblemServiceImpl extends ServiceImpl<GcProblemMapper, GcProblem> implements GcProblemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GcUserSaveFolderServiceImpl.class);
    @Override
    public List<GcCategory> selectCategoryList() {
        Integer level = TableConstant.COMMON_ZERO;
        return this.baseMapper.selectCategoryList(level);
    }

}
