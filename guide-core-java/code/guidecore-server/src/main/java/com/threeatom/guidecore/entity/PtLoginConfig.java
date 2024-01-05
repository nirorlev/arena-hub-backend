package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @title: PtLoginConfig
 * @projectName jeeplus
 * @description: TODO
 * @date 2022/12/9/00912:32
 */
@Data
public class PtLoginConfig implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer masterId;
    private String ptRootUrl;
    private String clientId;
    private String oauthToken;
    private String userUrl;
    private String groups;
    private String logOut;
    private String clientSecret;
    private String logOutUrl;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;

}
