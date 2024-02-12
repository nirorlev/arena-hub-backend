package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@ApiModel(value = "GcUserAccessExt对象", description = "")
@Data
public class GcUserAccessExt implements Serializable {

    /**
     * 2021-10-09,改造成登录记录后需将points删除，重置功能需重构
     */
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userAccessId;

    private Integer managerId;

    private Date logInTime;

    private BigDecimal points;

    private Date lastBatchemailTime;

    public GcUserAccessExt() {}

    public GcUserAccessExt(Integer userAccessId, Date logInTime, Integer managerId) {
        this.userAccessId = userAccessId;
        this.logInTime = logInTime;
        this.managerId = managerId;
    }
}
