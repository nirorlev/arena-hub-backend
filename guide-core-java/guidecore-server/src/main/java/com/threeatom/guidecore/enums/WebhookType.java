package com.threeatom.guidecore.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum WebhookType {
    POWTOON_PUBLISHED("powtoon_published");

    private final String type;

    public static Optional<WebhookType> fromString(String type) {
        return Arrays.stream(values())
            .filter(value -> value.getType().equals(type))
            .findFirst();
    }
}
