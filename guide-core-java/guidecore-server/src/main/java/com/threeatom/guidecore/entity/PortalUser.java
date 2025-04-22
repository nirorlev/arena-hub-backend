package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.UserOrgRole;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@TableName(value = "portal_user", autoResultMap = true)
public class PortalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    private Integer userId;
    @EqualsAndHashCode.Include
    private Integer masterId;

    @TableField(value = "role", typeHandler = EnumTypeHandler.class)
    @EqualsAndHashCode.Include
    private UserOrgRole role;

    private OffsetDateTime createdDate = OffsetDateTime.now();

    @EqualsAndHashCode.Include
    private OffsetDateTime syncedDate = OffsetDateTime.now();

    public boolean isOrgAdmin() {
        return UserOrgRole.ORG_ADMIN.equals(role);
    }

    public boolean isGroupAdmin() {
        return UserOrgRole.ADMIN.equals(role);
    }

    public boolean isMember() {
        return !isOrgAdmin() && !isGroupAdmin();
    }
}
