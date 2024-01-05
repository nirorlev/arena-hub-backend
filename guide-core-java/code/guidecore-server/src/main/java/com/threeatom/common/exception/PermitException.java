package com.threeatom.common.exception;

/**
 * @author PC
 * @title: PermitException
 * @projectName uploadServer
 * @description: TODO
 * @date 2023/8/2919:44
 */
public class PermitException extends RuntimeException  {

    private int code = 510;

    private Exception exception = null;


    public PermitException(String message) {
        this.code = 510;
    }

    public PermitException(int code, String message) {
        super(message);
        this.code = code;
    }

    public PermitException(int code, String message, Exception exception) {
        super(message);
        this.code = code;
        this.exception = exception;
    }

    public PermitException(String message, Exception exception) {
        super(message);
        this.code = 510;
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
