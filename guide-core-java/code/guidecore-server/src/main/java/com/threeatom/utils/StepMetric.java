package com.threeatom.utils;

import com.threeatom.common.exception.ValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum StepMetric {
    SECOND('s', "second"),
    MINUTE('m', "minute"),
    HOUR('h', "hour"),
    DAY('d', "day"),
    MONTH('M', "month");

    private final char symbol;
    private final String value;

    public static StepMetric fromSymbol(char symbol) {
        for (StepMetric metric : values()) {
            if (metric.getSymbol() == symbol) {
                return metric;
            }
        }

        throw new ValidationException(String.format("Invalid unit: '%s' for analytics step value", symbol));
    }
}