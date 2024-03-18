package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Administrator
 * @title: PermitConfiguration
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/9/00910:33
 */
@Configuration
@PropertySource(value = "classpath:system.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "permit")
@Data
public class PermitConfiguration {

    @Value("${permit.apiKey}")
    private String apiKey;

    @Value("${permit.pdpAddress}")
    private String pdpAddress;

    @Value("${permit.permitSwitch}")
    private Integer permitSwitch;
}
