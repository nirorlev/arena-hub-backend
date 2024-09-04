package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.UserOrgRole;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.Data;
import org.apache.ibatis.type.EnumTypeHandler;

@Data
@TableName(value = "portal_user", autoResultMap = true)
public class PortalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private Integer masterId;

    @TableField(value = "role", typeHandler = EnumTypeHandler.class)
    private UserOrgRole role;

    private OffsetDateTime createdDate = OffsetDateTime.now();
    private OffsetDateTime syncedDate = OffsetDateTime.now();

    public boolean isOrgAdmin() {
        return UserOrgRole.ORG_ADMIN.equals(role);
    }
}
