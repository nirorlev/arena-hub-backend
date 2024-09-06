package com.threeatom.guidecore.enums;


import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum UserOrgRole {
    LIMITED_MEMBER("limitedMember"),
    MEMBER("member"),
    ADMIN("admin"),
    ORG_ADMIN("orgAdmin");

    private static final List<UserOrgRole> ADMIN_ROLES = List.of(
        UserOrgRole.ADMIN
        , UserOrgRole.ORG_ADMIN
    );
    private static final List<UserOrgRole> MEBMER_ROLES = List.of(
        UserOrgRole.LIMITED_MEMBER
        , UserOrgRole.MEMBER
    );

    private final String role;

    public static UserOrgRole fromRoleId(String roleId) {
        return Arrays.stream(values())
            .filter(role -> role.getRole().equals(roleId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown org role: " + roleId));
    }

    public boolean isAdmin() {
        return ADMIN_ROLES.contains(this);
    }

    public boolean isMember() {
        return MEBMER_ROLES.contains(this);
    }
}
