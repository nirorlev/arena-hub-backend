package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.UserGroupRole;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.EnumTypeHandler;

@Getter
@Setter
@TableName(value = "user_groups", autoResultMap = true)
public class UserGroup {

    @TableId
    private GroupToUserPk id;

    @TableField(value = "role", typeHandler = EnumTypeHandler.class)
    private UserGroupRole role;

    @TableField(exist = false)
    private Group group;

    private OffsetDateTime createdTime = OffsetDateTime.now();
    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
