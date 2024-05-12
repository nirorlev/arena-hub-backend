package com.threeatom.common.permit.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermitAction {
    ACCESS_ANALYTICS("accessanalytics");

    private final String permitAction;

    @Override
    public String toString() {
        return permitAction;
    }
}
