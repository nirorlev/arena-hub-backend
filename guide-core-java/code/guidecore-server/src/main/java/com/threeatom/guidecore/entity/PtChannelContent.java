package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
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
 * @since 2019-11-11
 */
@Data
@ApiModel(value = "PtChannelContent", description = "")
@TableName(autoResultMap = true)
public class PtChannelContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer channelId;

    private Date updateTime;

    private Date createTime;

    private Integer fileId;

    private Integer playlistId;

    private Integer sectionId;

    @JSONField(deserialize = false)
    private Integer contentOrder;

    @TableField(exist = false)
    private String videoThumbNailUrl;

    @TableField(exist = false)
    private SysFile videoFile;

    @ApiModelProperty("课程tag标签")
    @TableField(value = "course_tags", typeHandler = FastJsonArrayTypeHandler.class, exist = false)
    private JSONArray courseTags = new JSONArray();
}
