package com.threeatom.config;

import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketHandler;
import com.threeatom.guidecore.websocket.server.GuidecoreMessageWebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(webSocketHandler(), "/guidecore/messageServer")
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
