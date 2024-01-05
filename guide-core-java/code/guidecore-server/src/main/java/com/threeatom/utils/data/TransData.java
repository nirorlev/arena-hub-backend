package com.threeatom.utils.data;

import lombok.Data;

/**
 * @author Administrator
 * @title: TransData
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/11/01115:32
 */
@Data
public class TransData {
    /**
     * 原文
     */
    private String src;
    /**
     * 译文
     */
    private String dst;
}
