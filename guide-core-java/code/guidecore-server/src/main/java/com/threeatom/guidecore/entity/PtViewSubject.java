package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.util.Date;
import lombok.Data;

/**
 * @author PC
 * @title: PtViewSubject
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/5/2514:34
 */
@ApiModel(value = "PtViewSubject", description = "")
@TableName(autoResultMap = true)
@Data
public class PtViewSubject {

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
