package com.threeatom.system.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class SysCaptionRequest {

    private JSONArray ymLanguage;

    private String language;

    private String fileUrl;

    private String notifyUrl;

    private String resultType;

    private Integer wordsLimit;

    private Integer captionId;
}
