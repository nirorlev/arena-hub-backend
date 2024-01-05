package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author PC
 * @title: GcSubjectComplete
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/12/810:50
 */
@Data
public class GcSubjectComplete implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 进度
     */
    private Integer inProgress;

    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * 完成状态 1完成 0未开始 2已开始
     */
    private Integer subjectState;

    /**
     * 门户id
     */
    private Integer masterId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
