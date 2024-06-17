package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Data Transfer Object representing a Channel")
@Getter
@Setter
public class ChannelDto {
    @ApiModelProperty(value = "Unique identifier of the Channel")
    private Integer id;

    @ApiModelProperty(value = "Title of the Channel")
    private String title;

    @ApiModelProperty(value = "Slug of the Channel")
    private String slug;

    @ApiModelProperty(value = "Description of the Channel")
    private String desc;

    @ApiModelProperty(value = "Owner of the Channel")
    private OwnerDto owner;

    @ApiModelProperty(value = "Public visibility status of the Channel")
    private boolean isPublic;

    @ApiModelProperty(value = "URL of the Channel's avatar")
    private String avatarUrl;

    @ApiModelProperty(value = "URL of the Channel's background")
    private String backgroundUrl;

    @ApiModelProperty(value = "Total number of subscribers of the Channel")
    private Integer totalSubscribers;

    @ApiModelProperty(value = "Creation time of the Channel")
    private Date createTime;

    @ApiModelProperty(value = "Last update time of the Channel")
    private Date updateTime;
}