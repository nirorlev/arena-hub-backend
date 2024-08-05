package com.threeatom.guidecore.exception;

public class LicenseLimitExceededException extends RuntimeException {
    public LicenseLimitExceededException(String message) {
        super(message);
    }
}
