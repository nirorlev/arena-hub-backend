package com.threeatom.guidecore.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 常见问题
 *
 * @author huangpei
 * @Date 2021-10-26
 */
@Data
public class GcProblem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 常见问题标题
     */
    private String title;

    /**
     * 常见问题内容
     */
    private String context;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 0为关闭
     */
    private Integer status;
}
