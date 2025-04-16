package com.threeatom.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sitemap")
@Data
public class SiteMapConfiguration {
    @Value("${sitemap.defaultId}")
    private Integer defaultId;

    @Value("${sitemap.siteUrl}")
    private String siteUrl;
}
