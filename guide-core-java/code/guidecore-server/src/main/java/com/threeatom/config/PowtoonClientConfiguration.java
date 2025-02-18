package com.threeatom.config;

import feign.RequestInterceptor;
import feign.form.FormEncoder;
import feign.form.spring.SpringFormEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PowtoonClientConfiguration {
    @Bean
    public FormEncoder formEncoder() {
        return new SpringFormEncoder();
    }

    @Bean
    public RequestInterceptor authorizationInterceptor() {
        return requestTemplate -> {
            log.info("Feign Request: {} {}", requestTemplate.method(), requestTemplate.url());
            log.info("Headers: {}", requestTemplate.headers());
            if (requestTemplate.body() != null) {
                log.info("Body: {}", new String(requestTemplate.body(), StandardCharsets.UTF_8));
            }
        };
    }
}
