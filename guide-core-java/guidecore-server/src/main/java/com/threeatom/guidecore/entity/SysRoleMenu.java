package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import java.util.Date;
import lombok.Data;

@Data
public class SysRoleMenu {
    private Integer id;

    private Integer roleId;

    private JSONArray menuIds;

    private Date createTime;

    private Date updateTime;
}
