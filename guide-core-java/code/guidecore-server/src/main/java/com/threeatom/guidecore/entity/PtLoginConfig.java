package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class PtLoginConfig implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer masterId;
    private String ptRootUrl;
    private String clientId;
    private String oauthToken;
    private String userUrl;
    @TableField("\"groups\"")
    private String groups;
    private String logOut;
    private String clientSecret;
    private String logOutUrl;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
