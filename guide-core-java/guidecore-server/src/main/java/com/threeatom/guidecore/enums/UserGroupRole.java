package com.threeatom.guidecore.enums;


import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserGroupRole {
    GROUP_MEMBER("groupMember"),
    GROUP_ADMIN("groupAdmin"),
    ORG_ADMIN("orgAdmin");

    public static UserGroupRole fromRoleId(String roleId) {
        return Arrays.stream(values())
                .filter(role -> role.getRole().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown group role: " + roleId));
    }

    private final String role;
}
