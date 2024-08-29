package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for auth code")
public class AuthTokenDto {

    @ApiModelProperty(value = "Authorisation code for Powtoon authorisation service")
    private String code;
}
