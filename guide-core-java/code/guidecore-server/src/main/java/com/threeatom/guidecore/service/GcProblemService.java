package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcFaq;
import com.threeatom.guidecore.entity.GcKnowledgebase;
import com.threeatom.guidecore.entity.GcProblem;

import java.util.List;

/**
 * 查询常见问题
 *
 * @author huangpei
 * @Date 2021-10-26
 */
public interface GcProblemService extends IService<GcProblem> {
    List<GcCategory> selectCategoryList();
}
