package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 * @title: Permissions
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/1/00110:52
 */
@Data
public class Permissions {

    private List<Groups> groups;

    private Org org;

    private List<Groups> managed_groups;
}
