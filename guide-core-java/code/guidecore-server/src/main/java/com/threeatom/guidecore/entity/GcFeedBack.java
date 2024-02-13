package com.threeatom.guidecore.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 系统反馈
 *
 * @author huangpei
 * @Date 2021-10-26
 */
@Data
@TableName(autoResultMap = true)
public class GcFeedBack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 类型 0为反馈 1为联系
     */
    private Integer type;

    /**
     * 反馈或联系信息
     */
    private String context;

    /**
     * 反馈文件Json
     */
    @TableField(value = "file_json", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray fileJson;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}
