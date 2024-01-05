package com.threeatom.config;

import com.threeatom.common.yml.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Administrator
 * @title: AwsSignedConfiguration
 * @projectName book
 * @description: TODO
 * @date 2022/7/14/01415:09
 */
@Configuration
@PropertySource(value="classpath:system.yml",factory= YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "uploadsignurl")
@Data
public class AwsUploadSignUrlConfiguration {

    @Value("${uploadsignurl.distributionDomain:stage.store.demoguide.xyz}")
    private String distributionDomain;

    @Value("${uploadsignurl.privateKeyFilePath:dev-stage_signed_links_private_key.der}")
    private String privateKeyFilePath;

    @Value("${uploadsignurl.keyPairId:K1JQYEVI2UZJ98}")
    private String keyPairId;

    @Value("${uploadsignurl.limitToIpAddressCIDR:0.0.0.0/0}")
    private String limitToIpAddressCIDR;
}
