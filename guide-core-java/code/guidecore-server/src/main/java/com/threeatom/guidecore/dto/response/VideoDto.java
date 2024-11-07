package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "Data Transfer Object representing a Video")
@Getter
@Setter
public class VideoDto {
    @ApiModelProperty(value = "Unique identifier of the Video")
    private Integer id;

    @ApiModelProperty(value = "Video name")
    private String name;

    @ApiModelProperty(value = "Video description")
    private String description;

    @ApiModelProperty(value = "Video source type index")
    private Integer fileTypeIndex;

    @ApiModelProperty(value = "Video url")
    private String fileUrl;

    @ApiModelProperty(value = "Video snapshot url")
    private String snapshotUrl;

    @ApiModelProperty(value = "Video length in seconds")
    private Integer duration;

    @ApiModelProperty(value = "Creation time of the Video")
    private OffsetDateTime createTime;

    @ApiModelProperty(value = "Last update time of the Video")
    private OffsetDateTime updateTime;
}