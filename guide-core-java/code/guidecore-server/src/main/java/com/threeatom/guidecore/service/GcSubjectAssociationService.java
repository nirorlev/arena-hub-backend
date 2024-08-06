package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import java.util.List;

public interface GcSubjectAssociationService extends IService<GcSubjectAssociation> {

    public Integer selectCount(Integer subjectId, Integer masterId);

    List<GcSubjectAssociation> selectSubsByMasterId(Integer masterId);

    Integer selectPackageStatus(Integer masterId);
}
