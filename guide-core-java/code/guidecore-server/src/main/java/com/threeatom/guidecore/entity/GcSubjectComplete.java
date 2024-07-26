package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class GcSubjectComplete implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer inProgress;

    private Integer subjectId;

    private Integer subjectState;

    private Integer masterId;

    private Integer userId;

    private Date createTime;

    private Date updateTime;
}
