package com.threeatom.guidecore.service.bll;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcVideo;

public interface GcVideoServiceBll extends IService<GcVideo>{
	
	/***
	 * 获取视频的上级topic subject等信息
	 * @param vid
	 * @return
	 */
	public Map<String,Object> getVideoSubjectInfo(Integer vid);

}
