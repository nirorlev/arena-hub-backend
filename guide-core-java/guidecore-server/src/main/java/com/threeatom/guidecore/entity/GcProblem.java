package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class GcProblem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String context;

    private Date createTime;

    private Date updateTime;

    private Integer status;
}
