//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.threeatom.common.exception;

public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final int ADMIN_CONFIG_ERROR = 10000;
    public static final int WEAPP_ERROR = 10001;
    private int code = 10000;
    private Exception exception = null;

    public SystemException() {
        this.code = 10000;
    }

    public SystemException(String message) {
        super(message);
        this.code = 10000;
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(int code, String message, Exception exception) {
        super(message);
        this.code = code;
        this.exception = exception;
    }

    public SystemException(String message, Exception exception) {
        super(message);
        this.code = 10000;
        this.exception = exception;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Exception getException() {
        return this.exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
