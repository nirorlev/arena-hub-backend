package com.threeatom.common.aws.entity;

import com.threeatom.common.aws.constan.SystemCode;

import java.io.Serializable;

public class ResultVO<T> implements Serializable {

    private boolean success = true;
    private String code;
    private String message;
    private T data;

    public ResultVO() {

    }

    public ResultVO(boolean success, String message) {
        this(success, null, message, null);
    }

    public ResultVO(boolean success, String message, T data) {
        this(success, null, message, data);
    }

    public ResultVO(boolean success, String code, String message, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ResultVO<T> success() {
        return new ResultVO(true, SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMessage(),
                null);
    }


    public static <T> ResultVO<T> success(T data) {
        return new ResultVO(true, SystemCode.SUCCESS.getCode(), SystemCode.SUCCESS.getMessage(),
                data);
    }


    public static <T> ResultVO<T> fail() {
        return new ResultVO(false, SystemCode.FAILED.getCode(), SystemCode.FAILED.getMessage(),
                null);
    }


    public static <T> ResultVO<T> fail(String code, String message) {
        return new ResultVO(false, code, message, null);
    }

    public static <T> ResultVO<T> fail(SystemCode systemCode) {
        return new ResultVO(false, systemCode.getCode(), systemCode.getMessage(), null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
