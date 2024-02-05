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
@PropertySource(value="classpath:system.yml",factory= YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "permit")
@Data
public class PermitConfiguration {

    @Value("${permit.apiKey:permit_key_ZW0KTH0icmdLZbJvG1fDN4JUMNS4OMMnRZBC3gyj7qSmkisJZR4K2ngtQs1932Mam5qIOpF5ySxy5tegMZXR8y}")
    private String apiKey;

    @Value("${permit.pdpAddress:http://47.111.191.189:7766}")
    private String pdpAddress;

    @Value("${permit.permitSwitch:0}")
    private Integer permitSwitch;
}
