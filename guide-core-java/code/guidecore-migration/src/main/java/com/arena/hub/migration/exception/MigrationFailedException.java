package com.arena.hub.migration.exception;

public class MigrationFailedException extends RuntimeException {
    public MigrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
