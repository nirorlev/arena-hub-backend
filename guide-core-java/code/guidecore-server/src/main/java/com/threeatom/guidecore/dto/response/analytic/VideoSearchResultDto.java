package com.threeatom.guidecore.dto.response.analytic;

import com.threeatom.guidecore.dto.response.UserDetailsDto;
import com.threeatom.guidecore.enums.VideoFileProvider;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a result of video search by a given query")
public class VideoSearchResultDto {
    @ApiModelProperty(value = "Id of a video")
    private Integer id;

    @ApiModelProperty(value = "Title of a video")
    private String title;

    @ApiModelProperty(value = "Thumb url of a video")
    private String thumbUrl;

    @ApiModelProperty(value = "Video content source details")
    private VideoFileProvider source;

    @ApiModelProperty(value = "private video flag")
    private boolean isPrivate;

    @ApiModelProperty(value = "Video content origiin details")
    private ContentOriginDto origin;
    @ApiModelProperty(value = "Video content owner details")
    private UserDetailsDto owner;

    @ApiModelProperty(value = "Video content created date and time")
    private OffsetDateTime created;
    @ApiModelProperty(value = "Video content updated date and time")
    private OffsetDateTime updated;
}
