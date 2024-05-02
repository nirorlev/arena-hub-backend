package com.threeatom.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AnalyticsStepUtil {

    private static final String STEP_FORMAT = "%s %s";

    public String parseAnalyticsStep(String stepValue) {
        int value = Integer.parseInt(stepValue.substring(0, stepValue.length() - 1));
        char unit = stepValue.charAt(stepValue.length() - 1);

        return getAnalyticsStep(unit, value);
    }

    private String getAnalyticsStep(char unit, int value) {
        return String.format(STEP_FORMAT, value, StepMetric.fromSymbol(unit).getValue());
    }
}