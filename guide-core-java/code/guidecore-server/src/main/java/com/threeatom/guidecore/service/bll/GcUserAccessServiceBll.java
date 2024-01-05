package com.threeatom.guidecore.service.bll;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;
import com.threeatom.guidecore.entity.GcUserAccess;

public interface GcUserAccessServiceBll extends IService<GcUserAccess>{
	
	
	/***
	 * 给用户添加点数
	 * @return
	 */
	public BigDecimal addPoints(Integer userAccessId,BigDecimal point);
	/***
	 * 扣除点数
	 * @param userAccessId
	 * @param point
	 * @return
	 */
	public BigDecimal reducePoints(Integer userAccessId,BigDecimal point);
	/***
	 * 获取用户当前的分数
	 * @param userAccessId
	 * @return
	 */
	public BigDecimal getCurrentUserPoints(Integer userAccessId);

}
