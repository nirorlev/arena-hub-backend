package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.threeatom.guidecore.enums.UserGroupRole;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "user_groups", autoResultMap = true)
public class UserGroup {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private UserGroupRole role;
    private Integer userId;
    private Integer groupId;

    @TableField(exist = false)
    private Group group;

    private OffsetDateTime updatedTime = OffsetDateTime.now();
}
