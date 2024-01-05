//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.guidecore.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threeatom.guidecore.entity.GcSubjectAssociation;

public interface GcSubjectAssociationMapper extends BaseMapper<GcSubjectAssociation> {
	
	Integer deleteGcSubjectAssociation(Integer subjectId,Integer masterId);
	
	long updateOrder(Integer order, Integer subjectId, Integer masterId);
	
	Integer selectCount(Integer subjectId,Integer masterId);
	
	long bulkUpdatOrderByMasterIdAndSubjetId(List<GcSubjectAssociation> saList);

	List<GcSubjectAssociation> selectSubsByMasterId(Integer masterId);

	Integer selectPackageStatus(Integer masterId);
}
