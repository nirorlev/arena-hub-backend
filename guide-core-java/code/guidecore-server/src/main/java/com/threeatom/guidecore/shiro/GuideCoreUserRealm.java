package com.threeatom.guidecore.shiro;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.threeatom.common.jwt.JwtToken;
import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.entity.GcManager;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.service.GcManagerService;
import com.threeatom.guidecore.service.GcUserService;

public class GuideCoreUserRealm extends AuthorizingRealm{

	private static final Logger LOGGER = LoggerFactory.getLogger(GuideCoreUserRealm.class);
	
	
	@Autowired
	@Lazy
	private GcManagerService managerService;
	
	@Autowired
	@Lazy
	private GcUserService userService;

	
	 @Override
	 public boolean supports(AuthenticationToken token) {
	     return token instanceof JwtToken;
	 }
	
	
	 
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
		LOGGER.info("guidecore授权");
		
		String token=principals.toString();
		LOGGER.info(token);
		String client=JwtUtil.getValueByToken(token, "client");
		String role=JwtUtil.getValueByToken(token, "role");

		info.addRole(role);
		info.addRole(client);
		
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// TODO Auto-generated method stub
		String tokenStr=(String)token.getCredentials();
		LOGGER.info("guidecore登录认证");
		
		LOGGER.info(tokenStr);
		
		String client=JwtUtil.getValueByToken(tokenStr, "client");
		
		//验证token
		switch(client) {
			case "web":
			case "native":
				return doWebClient(tokenStr);
			/*case "weapp":
				return doWeappClient(tokenStr);*/
			default:
				return null;
		}
		
	}
	/***
	 * 根据登录点的端口来判定用什么方法验证
	 * @param tokenStr
	 */
			private SimpleAuthenticationInfo doWebClient(String tokenStr) {
		String role=JwtUtil.getValueByToken(tokenStr, "role");
		Integer id=Integer.parseInt(JwtUtil.getValueByToken(tokenStr, "uid"));
		switch(role) {
			case "manager":
				GcManager manager=managerService.getManagerByIdCache(id);
				if(manager==null) 
					throw new AuthenticationException("没有找到账户");
				
				JwtUtil.verifyToken(tokenStr, manager.getPassword());
				break;

			case "user":
				GcUser user=userService.getUserByIdCache(id);

				if(user==null) 
					throw new AuthenticationException("没有找到账户");
				JwtUtil.verifyToken(tokenStr, user.getPassword());
				break;
		}
		
		return new SimpleAuthenticationInfo(tokenStr, tokenStr, this.getName());

	}
}
