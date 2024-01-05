package com.threeatom.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @title: StripePayConfiguration
 * @projectName guidecore
 * @description: TODO
 * @date 2022/6/30/03014:43
 */

@Configuration
@ConfigurationProperties(prefix = "sitemap")
@Data
public class SiteMapConfiguration {
    @Value("${sitemap.defaultId:207}")
    private Integer defaultId;

    @Value("${sitemap.siteUrl:stage.demoguide.xyz}")
    private String siteUrl;
}
