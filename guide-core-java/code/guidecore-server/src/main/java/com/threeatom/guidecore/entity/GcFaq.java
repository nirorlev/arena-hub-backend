package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.system.entity.SysFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 视频下的event
 * </p>
 *
 * @author qiaoxide
 * @since 2019-11-18
 */
@ApiModel(value="GcFaq", description="常见问题")
@Data
public class GcFaq implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "常见问题类别")
    private String faqSection;

    @ApiModelProperty(value = "常见问题名称")
    private String faqTitle;

    @ApiModelProperty(value = "问题内容")
    private String faqContent;

    @ApiModelProperty(value = "上传人实体")
    private Integer del_flag;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "父级Id")
    private Integer faqId;


}
