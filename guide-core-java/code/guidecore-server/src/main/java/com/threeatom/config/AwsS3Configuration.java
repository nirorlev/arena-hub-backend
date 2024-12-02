package com.threeatom.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Getter
public class AwsS3Configuration {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretAccessKey}")
    private String secretKey;

    @Value("${feBucketName}")
    private String feBucketName;
}
