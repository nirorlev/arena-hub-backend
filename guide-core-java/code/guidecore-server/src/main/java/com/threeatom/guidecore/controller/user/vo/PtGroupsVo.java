package com.threeatom.guidecore.controller.user.vo;

import java.util.List;
import lombok.Data;

/**
 * @author PC
 * @title: PtGroupsVo
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/510:06
 */
@Data
public class PtGroupsVo {
    private Integer count;

    private List<Groups> results;
}
