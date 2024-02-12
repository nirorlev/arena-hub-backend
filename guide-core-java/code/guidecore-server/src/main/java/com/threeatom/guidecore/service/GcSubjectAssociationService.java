package com.threeatom.guidecore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 */
public interface GcSubjectAssociationService extends IService<GcSubjectAssociation> {

    public Integer selectCount(Integer subjectId, Integer masterId);

    List<GcSubjectAssociation> selectSubsByMasterId(Integer masterId);

    Integer selectPackageStatus(Integer masterId);
}
