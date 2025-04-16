package com.threeatom.client.dto;

import com.threeatom.guidecore.enums.UserOrgRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrgDto {
    private String id;
    private String roleId;

    public UserOrgRole getRoleId() {
        return UserOrgRole.fromRoleId(roleId);
    }
}
