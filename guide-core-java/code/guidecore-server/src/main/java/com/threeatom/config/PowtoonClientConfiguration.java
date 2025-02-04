package com.threeatom.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PowtoonClientConfiguration {
    @Bean
    public RequestInterceptor authorizationInterceptor() {
        return template -> template.header("User-Agent", "arena");
    }
}
