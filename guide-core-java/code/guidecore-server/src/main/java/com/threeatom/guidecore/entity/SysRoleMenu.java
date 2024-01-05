package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.Date;

/**
 * @author Administrator
 * @title: Sys_role_menu
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:25
 */
@Data
public class SysRoleMenu {
    private Integer id;

    private Integer roleId;

    private JSONArray menuIds;

    private Date createTime;

    private Date updateTime;
}
