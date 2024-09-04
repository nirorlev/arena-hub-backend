package com.threeatom.guidecore.enums;


import java.util.Arrays;
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

    public static UserOrgRole fromRoleId(String roleId) {
        return Arrays.stream(values())
                .filter(role -> role.getRole().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + roleId));
    }

    private final String role;
}
