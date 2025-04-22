package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcProblem;
import java.util.List;

public interface GcProblemService extends IService<GcProblem> {
    List<GcCategory> selectCategoryList();
}
