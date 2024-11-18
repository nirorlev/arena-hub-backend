package com.threeatom.guidecore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import java.util.List;

public interface GcSubjectAssociationMapper extends BaseMapper<GcSubjectAssociation> {

    Integer deleteGcSubjectAssociation(Integer subjectId, Integer masterId);

    long updateOrder(Integer order, Integer subjectId, Integer masterId);

    long bulkUpdatOrderByMasterIdAndSubjetId(List<GcSubjectAssociation> saList);

    List<GcSubjectAssociation> selectSubsByMasterId(Integer masterId);

    Integer selectPackageStatus(Integer masterId);
}
