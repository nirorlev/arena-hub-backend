package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.guidecore.entity.GcMaster;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:system.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "channel")
public class ChannelConfiguration {

    private List<GcMaster> channelIdList;

    public List<GcMaster> getChannelIdList() {
        return channelIdList;
    }

    public void setChannelIdList(List<GcMaster> channelIdList) {
        this.channelIdList = channelIdList;
    }
}
