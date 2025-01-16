package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import com.threeatom.guidecore.mapper.GcSubjectAssociationMapper;
import com.threeatom.guidecore.service.GcSubjectAssociationService;
import org.springframework.stereotype.Service;

@Service
public class GcSubjectAssociationServiceImpl
        extends ServiceImpl<GcSubjectAssociationMapper, GcSubjectAssociation>
        implements GcSubjectAssociationService {

    @Override
    public Integer selectPackageStatus(Integer masterId) {
        return this.baseMapper.selectPackageStatus(masterId);
    }
}
