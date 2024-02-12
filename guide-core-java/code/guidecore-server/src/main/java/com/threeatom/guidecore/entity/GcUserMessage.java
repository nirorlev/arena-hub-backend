package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.system.entity.SysFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author qiaoxide
 * @since 2019-12-09
 */
@Data
@ApiModel(value = "GcUserMessage对象", description = "")
public class GcUserMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "消息所有用户id")
    private Integer userId;

    @ApiModelProperty(value = "主站id")
    private Integer masterId;

    @ApiModelProperty(value = "目标用户id")
    private Integer targetUserId;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "文件id")
    private Integer fileId;

    @ApiModelProperty(value = "文件")
    @TableField(exist = false)
    private SysFile file;

    @ApiModelProperty(value = "读取状态")
    private Integer readState;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    private Integer newMesCount;
}
