package com.threeatom.common.permit.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermitResource {
    PORTAL("Portal");

    private final String permitValue;

    @Override
    public String toString() {
        return permitValue;
    }
}
