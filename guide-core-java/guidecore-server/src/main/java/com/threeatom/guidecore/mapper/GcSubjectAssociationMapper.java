package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import java.util.List;

public interface GcSubjectAssociationMapper extends BaseMapper<GcSubjectAssociation> {

    Integer deleteGcSubjectAssociation(Integer subjectId, Integer masterId);

    long bulkUpdatOrderByMasterIdAndSubjetId(List<GcSubjectAssociation> saList);

    Integer selectPackageStatus(Integer masterId);
}
