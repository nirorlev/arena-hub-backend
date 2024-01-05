package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import com.threeatom.guidecore.entity.GcMaster;
import com.threeatom.guidecore.entity.GcUserVideoAction;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * @author Administrator
 * @title: StripePayConfiguration
 * @projectName guidecore
 * @description: TODO
 * @date 2022/6/30/03014:43
 */

@Configuration
@PropertySource(value="classpath:system.yml",factory= YamlPropertySourceFactory.class)
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
