package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.UserOrgRole;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
@TableName(value = "portal_user", autoResultMap = true)
public class PortalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer masterId;
    private String role;

    private OffsetDateTime createdDate;
    private OffsetDateTime modifiedDate;

    public boolean isOrgAdmin() {
        return UserOrgRole.ORG_ADMIN.getRole().equals(role);
    }
}
