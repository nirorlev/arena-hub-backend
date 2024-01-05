package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;


@Configuration
@PropertySource(value="classpath:system.yml",factory= YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "metaurl")
@Data
public class metarielConfig {
    @Value("${metaurl.course}")
    private String course;

    @Value("${metaurl.courseStatics}")
    private String courseStatics;

    @Value("${metaurl.courseVideo}")
    private String courseVideo;

    @Value("${metaurl.channel}")
    private String channel;

    @Value("${metaurl.playlist}")
    private String playlist;

}
