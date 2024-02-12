package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:system.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "star")
public class CourseStarConfiguration {
    private List<GcUserVideoAction> starcourselist;

    public List<GcUserVideoAction> getStarcourselist() {
        return starcourselist;
    }

    public void setStarcourselist(List<GcUserVideoAction> starcourselist) {
        this.starcourselist = starcourselist;
    }
}
