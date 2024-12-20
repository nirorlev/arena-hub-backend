package com.threeatom.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Configuration {

    private String accessKey;
    private String secretAccessKey;
    private String frontendBucketName;
}


