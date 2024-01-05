package com.threeatom.guidecore.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcSubjectAssociation;
import com.threeatom.guidecore.mapper.GcSubjectAssociationMapper;
import com.threeatom.guidecore.service.GcSubjectAssociationService;

@Service
public class GcSubjectAssociationServiceImpl extends ServiceImpl<GcSubjectAssociationMapper, GcSubjectAssociation> implements GcSubjectAssociationService {

	@Override
	public Integer selectCount(Integer subjectId,Integer masterId) {
		
		return this.baseMapper.selectCount(subjectId,masterId);
	}

	@Override
	public List<GcSubjectAssociation> selectSubsByMasterId(Integer masterId) {
		// TODO Auto-generated method stub
		return this.baseMapper.selectSubsByMasterId(masterId);
	}

	@Override
	public Integer selectPackageStatus(Integer masterId) {
		return this.baseMapper.selectPackageStatus(masterId);
	}

}
