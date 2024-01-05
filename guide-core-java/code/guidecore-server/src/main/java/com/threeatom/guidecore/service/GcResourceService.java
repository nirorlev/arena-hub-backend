package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcEvent;
import com.threeatom.guidecore.entity.GcResource;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-19
 */
public interface GcResourceService extends IService<GcResource> {
	
	boolean saveResource(GcResource resource);
	
	boolean deleteResourcesByVids(List<Integer> vid);
	
	int getResourceNum(Integer masterId,List<Integer> subIds,Integer managerId);
	
	List<GcResource> getResByVid(Integer vid);
	

}
