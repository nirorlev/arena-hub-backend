package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Administrator
 * @title: AwsS3Configuration
 * @projectName uploadServer
 * @description: TODO
 * @date 2022/4/28/02816:39
 */
@Configuration
@PropertySource(value = "classpath:system.yml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "aws")
public class AwsS3Configuration {

    @Value("${aws.durationSeconds}")
    private Integer durationSeconds;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.roleArn}")
    private String roleArn;

    @Value("${aws.roleSessionName}")
    private String roleSessionName;

    @Value("${aws.bucketName}")
    private String bucketName;

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRoleArn() {
        return roleArn;
    }

    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    public String getRoleSessionName() {
        return roleSessionName;
    }

    public void setRoleSessionName(String roleSessionName) {
        this.roleSessionName = roleSessionName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
