package com.threeatom.guidecore.controller.user.vo;

import java.util.List;
import lombok.Data;

@Data
public class youtubeVo {

    private static final long serialVersionUID = 1L;

    // 字段名
    private List<String> vidList;
    // 过滤类型
    private List<String> logicType;
    // 值
    private List<String> values;
    // 过滤条件
    private List<Integer> subIds;
    private List<String> codes;
}
