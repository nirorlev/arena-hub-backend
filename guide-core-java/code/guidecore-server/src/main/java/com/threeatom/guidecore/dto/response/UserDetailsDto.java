package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Data Transfer Object representing an Owner")
@Getter
@Setter
public class UserDetailsDto {
    @ApiModelProperty(value = "Unique identifier of the Owner")
    private Integer id;

    @ApiModelProperty(value = "First name of the Owner")
    private String firstName;

    @ApiModelProperty(value = "Last name of the Owner")
    private String lastName;

    @ApiModelProperty(value = "URL of the Owner's avatar")
    private String thumbUrl;
}