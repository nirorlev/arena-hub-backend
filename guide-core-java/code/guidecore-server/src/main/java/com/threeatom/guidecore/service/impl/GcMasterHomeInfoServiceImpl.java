package com.threeatom.guidecore.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.GcAccess;
import com.threeatom.guidecore.entity.GcMasterHomeInfo;
import com.threeatom.guidecore.entity.GcVideo;
import com.threeatom.guidecore.mapper.GcMasterHomeInfoMapper;
import com.threeatom.guidecore.service.GcMasterHomeInfoService;
import com.threeatom.system.entity.SysFile;
import com.threeatom.system.entity.SysSystem;
import com.threeatom.system.service.SysFileService;


/**
 *
 * @author huangwenjun
 * @date 2021-01-09 14:44:12
 */
@Service
public class GcMasterHomeInfoServiceImpl extends ServiceImpl<GcMasterHomeInfoMapper, GcMasterHomeInfo> implements GcMasterHomeInfoService {
	
	@Autowired
    private SysFileService sysFileService;
	
	@Override
	@Transactional
	public boolean saveGcMasterHomeInfo(Integer masterId, List<GcMasterHomeInfo> infoList) {
		
		for(GcMasterHomeInfo info:infoList) {
			if(info.getId()==null) {
				
				//查重，如果master_id和name存在，但前端没有传id过来，则要获取id保存，否则会保存重复信息
				QueryWrapper<GcMasterHomeInfo> queryWrapper = new QueryWrapper<GcMasterHomeInfo>();
			    queryWrapper.eq("master_id", masterId);
			    queryWrapper.eq("name", info.getName());
			    GcMasterHomeInfo tmpOne =this.getOne(queryWrapper);
			    if(tmpOne!=null && tmpOne.getId()!=null) {
			    	info.setId(tmpOne.getId());
			    }
				 
				info.setMasterId(masterId);
			}
			
		}
		
		return this.saveOrUpdateBatch(infoList);
	}

	@Override
	public List<GcMasterHomeInfo> getGcMasterHomeInfoList(Integer masterId, List<String> nameList, SysSystem sys, HttpServletRequest request) {
		// TODO Auto-generated method stub
		QueryWrapper<GcMasterHomeInfo> queryWrapper=new QueryWrapper<GcMasterHomeInfo>();
		queryWrapper.eq("master_id", masterId);
		if(nameList!=null && nameList.size()>0) {
			queryWrapper.in("name", nameList);
		}
		List<GcMasterHomeInfo> list = this.list(queryWrapper);
		for (GcMasterHomeInfo homeInfo: list) {
			if(homeInfo.getFileId()==null) {
				continue;
			}
			SysFile file=sysFileService.getById(homeInfo.getFileId());
            if(file!=null) {
                file.setFullFileUrl(sysFileService.getResFullUrl(file, request));
                homeInfo.setFile(file);
                
            }
		}
		return list;
	}
	
//	@Override
//	public List<GcMasterHomeInfo> getGcMasterHomeInfoList(Integer masterId) {
		// TODO Auto-generated method stub
//		QueryWrapper<GcMasterHomeInfo> queryWrapper=new QueryWrapper<GcMasterHomeInfo>();
//		queryWrapper.eq("master_id", masterId);
//		return this.list(queryWrapper);
//	}

}