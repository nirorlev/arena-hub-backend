package com.threeatom.guidecore.websocket.server;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.threeatom.common.jwt.JwtUtil;
import com.threeatom.guidecore.entity.GcUser;
import com.threeatom.guidecore.entity.GcUserAccess;
import com.threeatom.guidecore.service.GcUserAccessService;
import com.threeatom.guidecore.service.GcUserService;

public class GuidecoreMessageWebSocketInterceptor implements HandshakeInterceptor{

	
	
    //前置拦截一般用来注册用户信息，绑定 WebSocketSession
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
       System.out.println("前置拦截~~");
       if(request instanceof ServletServerHttpRequest) {
    	   ServletServerHttpRequest serverHttpRequest=(ServletServerHttpRequest)request;
    	   
    	   Enumeration<String> params=serverHttpRequest.getServletRequest().getParameterNames();
    	   while(params.hasMoreElements()) {
    		   String paramName=params.nextElement();
    		   attributes.put(paramName, serverHttpRequest.getServletRequest().getParameter(paramName));
    	   }
    	   
    	   
    	   return userValide(attributes,serverHttpRequest.getServletRequest());
       }
       
        
       return true;
        
        
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        System.out.println("后置拦截~~");
    }
    
    private boolean userValide(Map<String, Object> attributes,HttpServletRequest request) {
		return false;
    }
    
    private <T> T getService(Class<T> clazz,HttpServletRequest request) {
    	WebApplicationContext applicationContext=WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
    	return applicationContext.getBean(clazz);
    }
}
