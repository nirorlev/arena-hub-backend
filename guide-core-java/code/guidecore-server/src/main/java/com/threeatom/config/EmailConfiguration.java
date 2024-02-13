package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.config.data.EmailConfigData;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:system.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "email")
public class EmailConfiguration {

    private EmailConfigData aliyunEmail;

    public EmailConfigData getAliyunEmail() {
        return aliyunEmail;
    }

    public void setAliyunEmail(EmailConfigData aliyunEmail) {
        this.aliyunEmail = aliyunEmail;
    }

    @Bean
    public EmailConfigData getEmailConfigData() {
        return this.aliyunEmail;
    }
}
