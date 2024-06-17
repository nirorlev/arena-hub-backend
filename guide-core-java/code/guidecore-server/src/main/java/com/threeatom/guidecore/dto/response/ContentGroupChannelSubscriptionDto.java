package com.threeatom.guidecore.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object representing a content group channel subscription")
public class ContentGroupChannelSubscriptionDto {

    @ApiModelProperty(notes = "The unique ID of the content group channel subscription")
    private Integer id;

    @ApiModelProperty(notes = "The title of the title")
    private String channelTitle;

    @ApiModelProperty(notes = "The slug of the channel")
    private String channelSlug;

    @ApiModelProperty(notes = "The ID of the channel")
    private String channelId;

    @ApiModelProperty(notes = "The URL of the channel image")
    private String channelImageUrl;

    @ApiModelProperty(notes = "The source of the content group course assignment")
    private ContentGroupCourseAssignmentSourceDto source;

    @ApiModelProperty(notes = "The date and time when the content group course assignment was last modified")
    private OffsetDateTime modifiedDate = OffsetDateTime.now();
}