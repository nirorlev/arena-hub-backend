package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketHandler;
import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;


@Configuration
@PropertySource(value="classpath:system.yml",factory=YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "star")
public class CourseStarConfiguration {
	private List<GcUserVideoAction> starcourselist;

	public List<GcUserVideoAction> getStarcourselist() {
		return starcourselist;
	}

	public void setStarcourselist(List<GcUserVideoAction> starcourselist) {
		this.starcourselist = starcourselist;
	}
}
