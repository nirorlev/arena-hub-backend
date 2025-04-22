package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcCategory;
import com.threeatom.guidecore.entity.GcProblem;
import com.threeatom.guidecore.mapper.GcProblemMapper;
import com.threeatom.guidecore.service.GcProblemService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GcProblemServiceImpl extends ServiceImpl<GcProblemMapper, GcProblem>
        implements GcProblemService {

    @Override
    public List<GcCategory> selectCategoryList() {
        Integer level = TableConstant.COMMON_ZERO;
        return this.baseMapper.selectCategoryList(level);
    }
}
