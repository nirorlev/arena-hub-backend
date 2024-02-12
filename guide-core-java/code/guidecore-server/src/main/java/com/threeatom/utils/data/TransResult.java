package com.threeatom.utils.data;

import java.util.List;
import lombok.Data;

/**
 * @author Administrator
 * @title: TransResult
 * @projectName guidecore
 * @description: TODO
 * @date 2021/11/11/01115:32
 */
@Data
public class TransResult {
    /**
     *翻译源语言
     */
    private String from;

    /**
     *译文语言
     */
    private String to;

    /**
     *翻译结果
     */
    private List<TransData> trans_result;
}
