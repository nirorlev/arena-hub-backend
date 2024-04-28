package com.threeatom.common.exception;

public class VideoPlaySegmentNotFoundException extends RuntimeException {

    public VideoPlaySegmentNotFoundException(String message) {
        super(message);
    }

    public VideoPlaySegmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
