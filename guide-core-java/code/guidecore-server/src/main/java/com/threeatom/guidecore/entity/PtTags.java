package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import lombok.Data;

/**
 * @author Administrator
 * @title: PtTags
 * @projectName jcasbin
 * @description: TODO
 * @date 2023/1/9/00910:25
 */
@ApiModel(value = "ptTags", description = "")
@TableName(autoResultMap = true)
@Data
public class PtTags {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 门户id
     */
    private Integer masterId;

    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * tag内容
     */
    private String tagText;

    /**
     * 1=课程tag 2=视频tag
     */
    @TableField(value = "`type`")
    private Integer type;

    /**
     * 排序
     */
    @TableField(value = "`order`")
    private Integer order;

    /**
     * 视频id
     */
    private Integer videoId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 资源id
     */
    private Integer resourceId;

    private Integer channelId;

    /**
     * 文件id
     */
    private Integer fileId;
}
