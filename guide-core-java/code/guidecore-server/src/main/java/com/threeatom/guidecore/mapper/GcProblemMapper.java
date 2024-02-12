package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcFaq;
import com.threeatom.guidecore.entity.GcKnowledgebase;
import com.threeatom.guidecore.entity.GcProblem;
import java.util.List;

public interface GcProblemMapper extends BaseMapper<GcProblem> {

    List<GcProblem> selectProblemList();

    List<GcFaq> selectFaqList(Integer delFlag);

    List<GcKnowledgebase> selectKnowledgebaseVideo(Integer delFlag);

    List<GcCategory> selectCategoryList(Integer level);
}
