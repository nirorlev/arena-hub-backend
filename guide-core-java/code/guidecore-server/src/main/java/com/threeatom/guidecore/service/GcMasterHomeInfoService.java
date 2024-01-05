package com.threeatom.guidecore.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcMasterHomeInfo;
import com.threeatom.system.entity.SysSystem;

/**
 * <p>
 * 主站点实例 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcMasterHomeInfoService extends IService<GcMasterHomeInfo> {


	public List<GcMasterHomeInfo> getGcMasterHomeInfoList(Integer masterId, List<String> nameList, SysSystem sys, HttpServletRequest request);

	public boolean saveGcMasterHomeInfo(Integer masterId, List<GcMasterHomeInfo> infoList);
	
	

}
