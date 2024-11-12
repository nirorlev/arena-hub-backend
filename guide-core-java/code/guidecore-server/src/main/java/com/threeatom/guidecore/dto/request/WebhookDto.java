package com.threeatom.guidecore.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Data Transfer Object for Webhook")
public class WebhookDto {
    @ApiModelProperty(notes = "The unique ID of the webhook event")
    private String id;
    @ApiModelProperty(notes = "The type of the webhook event")
    private String type;
    @ApiModelProperty(notes = "Webhook data")
    private JSONObject data;
}
