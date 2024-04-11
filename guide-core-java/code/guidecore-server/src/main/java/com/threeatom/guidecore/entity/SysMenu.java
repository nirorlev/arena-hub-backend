package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * @author Administrator
 * @title: SysMenu
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/3/10/01016:12
 */
@Data
public class SysMenu {
    @ApiModelProperty("业务id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("\"name\"")
    private String name;

    @TableField("\"key\"")
    private String key;

    private Integer parentId;

    @TableField("\"level\"")
    private Integer level;

    @TableField("\"state\"")
    private Integer state;

    private Integer sort;

    private Date createTime;

    private Date updateTime;

    private String remarks;

    /**
     * 修改后的名称
     */
    private String updateName;

    /**
     * 排序字段
     */
    @TableField("\"order\"")
    private Integer order;

    /**
     * 修改开关,1是 0否
     */
    private Integer updateSwitch;

    private Integer masterId;
}
