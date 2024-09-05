package com.threeatom.guidecore.constant;

import com.threeatom.guidecore.enums.UserOrgRole;
import java.util.List;

public class GroupsType {

    public static final String superAdmin = "superadmin";

    public static final List<UserOrgRole> MEMBERS = List.of(
            UserOrgRole.LIMITED_MEMBER
            , UserOrgRole.MEMBER
    );

    public static final List<UserOrgRole> ADMINS = List.of(
        UserOrgRole.ADMIN
            , UserOrgRole.ORG_ADMIN
    );
}
