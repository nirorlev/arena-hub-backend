package com.threeatom.guidecore.service.impl;

import com.threeatom.common.exception.SystemException;
import com.threeatom.guidecore.constant.TableConstant;
import com.threeatom.guidecore.entity.GcResource;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcResourceMapper;
import com.threeatom.guidecore.service.GcResourceService;
import com.threeatom.guidecore.service.GcSubjectService;
import com.threeatom.guidecore.service.GcVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import com.threeatom.guidecore.util.I18NUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-19
 */
@Service
public class GcResourceServiceImpl extends ServiceImpl<GcResourceMapper, GcResource> implements GcResourceService {

	@Autowired
	private GcVideoService videoService;
	@Autowired
	private GcSubjectService subjectService;
	
	@Override
	public boolean deleteResourcesByVids(List<Integer> vids) {
		// TODO Auto-generated method stub
		if(vids.size()<1) return true;
		QueryWrapper<GcResource> queryWrapper=new QueryWrapper<GcResource>();
		queryWrapper.in("video_id", vids);
		return this.remove(queryWrapper);
		
	}

	@Override
	public boolean saveResource(GcResource resource) {
		// TODO Auto-generated method stub
		//校验数据
		Integer vid=resource.getVideoId();
		GcVideo video= videoService.getById(vid);
		if(video==null) throw new SystemException(I18NUtil.get("video.not.exist"));
		//验证类型
		if(resource.getResourceType().equals(2)) {
			resource.setFileId(0);
		}
		
		return this.saveOrUpdate(resource);
	}

	@Override
	public int getResourceNum(Integer masterId,List<Integer> subIds,Integer managerId) {
		// TODO Auto-generated method stub
//
//		List<Integer> videoIds=videoService.getVideoIdsBy(subjectService.getSubjectIds(masterId));
//
//		if(videoIds.size()<1) return 0;
//		QueryWrapper<GcResource> queryWrapper=new QueryWrapper<GcResource>();
//		queryWrapper.in("video_id", videoIds);
//		return this.count(queryWrapper);
		Integer type = TableConstant.COMMON_ONE;
		Integer state = TableConstant.COMMON_ZERO;
		return this.baseMapper.countResourceNum(masterId,type,state,subIds,managerId);
	}

	@Override
	public List<GcResource> getResByVid(Integer vid) {
		// TODO Auto-generated method stub
//		QueryWrapper<GcResource> queryWrapper=new QueryWrapper<GcResource>();
//		queryWrapper.eq("video_id", vid);
//		return this.list(queryWrapper);
		return this.baseMapper.selectResListByVid(vid);
	}

}
