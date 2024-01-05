package com.threeatom.guidecore.service;

import com.threeatom.guidecore.entity.GcManager;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商户管理员 服务类
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-11
 */
public interface GcManagerService extends IService<GcManager> {

	
	GcManager getManagerByIdCache(Integer id);
	
	
	/**
	 * 注册
	 *
     * @param code
     * @param email
     * @param password
     * @param fName
     * @param lName
     * @return
	 */
	boolean createManager(Integer sysId, String code, String email, String password, String fName, String lName);
	
	/***
	 * 登陆
	 * @param email
	 * @param password
	 * @return
	 */
	String loginGetToken(String email,String password);
	
	GcManager getManagerByUsername(String username);

	boolean saveOrUpdateManager(GcManager manager);

}
