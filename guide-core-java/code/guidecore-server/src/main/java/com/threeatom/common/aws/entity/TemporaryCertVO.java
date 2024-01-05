package com.threeatom.common.aws.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TemporaryCertVO implements Serializable {

    private String region;

    private String bucketName;

    private String accessKey;

    private String secretKey;

    private String sessionToken;

    private Date expiration;

}