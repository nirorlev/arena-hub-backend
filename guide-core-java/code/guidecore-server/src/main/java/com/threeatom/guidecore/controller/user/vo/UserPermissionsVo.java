package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

@Data
public class UserPermissionsVo {

    private PermissionsVo permissions;

    private Profile profile;
}
