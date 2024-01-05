package com.threeatom.system.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

/**
 * @author Administrator
 * @title: CaptionRequest
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/5/00512:11
 */
@Data
public class SysCaptionRequest {

    /**
     * 语言
     */
    private JSONArray ymLanguage;

    /**
     * 传入云猫主语言
     */
    private String language;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 回调地址
     */
    private String notifyUrl;

    /**
     * 返回类型
     */
    private String resultType;

    /**
     * 最大行数
     */
    private Integer wordsLimit;

    /**
     * 文件id
     */
    private Integer captionId;
}
