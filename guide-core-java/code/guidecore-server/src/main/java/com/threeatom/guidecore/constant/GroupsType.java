package com.threeatom.guidecore.constant;

import com.threeatom.guidecore.enums.UserOrgRole;
import java.util.ArrayList;
import java.util.List;

public class GroupsType {

    public static final String orgAdmin = "orgAdmin";

    public static final String orgMember = "orgMember";
    public static final String limitedMember = "limitedMember";

    public static final String teamAdmin = "teamAdmin";

    public static final String teamMember = "teamMember";

    public static final String teamGuest = "teamGuest";

    public static final String superAdmin = "superadmin";

    public static final String admin = "admin";

    public static final String member = "member";

    public static final String groupMember = "groupMember";

    public static final String groupAdmin = "groupAdmin";

    public static final List<UserOrgRole> MEMBERS = List.of(
            UserOrgRole.LIMITED_MEMBER
            , UserOrgRole.MEMBER
    );

    public static final List<UserOrgRole> ADMINS = List.of(
        UserOrgRole.ADMIN
            , UserOrgRole.ORG_ADMIN
    );

    public static final List<String> superAdminList =
            new ArrayList<String>() {
                {
                    this.add(superAdmin);
                }
            };

    public static final List<String> groupList =
            new ArrayList<String>() {
                {
                    this.add(orgAdmin);
                    this.add(orgMember);
                }
            };
}
