package com.threeatom.config;

import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketHandler;
import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketInterceptor;


@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer{

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addHandler(webSocketHandler(),"/guidecore/messageServer")
		.setAllowedOrigins("*")
		.addInterceptors(webSocketInterceptor());
		
	}
	@Bean
	public GuidecoreMessageWebSocketHandler webSocketHandler() {
		return new GuidecoreMessageWebSocketHandler();
	}
	
	public GuidecoreMessageWebSocketInterceptor webSocketInterceptor() {
		return new GuidecoreMessageWebSocketInterceptor();
	}
	
	
	

}
