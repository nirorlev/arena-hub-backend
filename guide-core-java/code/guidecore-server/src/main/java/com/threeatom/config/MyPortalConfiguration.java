package com.threeatom.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "subscription")
@Data
public class MyPortalConfiguration {
    @Value("${subscription.id:}")
    private List<Integer> id;

    @Value("${subscription.freeBookSummaryPortalId:117}")
    private Integer freeBookSummaryPortalId;

    @Value("${subscription.freeBookSummaryCode:#{null}}")
    private List<String> FreeBookSummary;

}
