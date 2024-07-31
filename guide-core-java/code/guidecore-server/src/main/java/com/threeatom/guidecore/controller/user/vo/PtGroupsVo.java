package com.threeatom.guidecore.controller.user.vo;

import java.util.List;
import lombok.Data;

@Data
public class PtGroupsVo {
    private Integer count;

    private List<Groups> results;
}
