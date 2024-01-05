package com.threeatom.common.aws.constan;

public enum SystemCode {

    SUCCESS("200", "成功"),
    FAILED("100000", "系统异常"),

    PARAMETER_EXCEPTION("300000", "请求参数异常"),
    PARAMETER_MISS("300100", "请求参数缺失"),
    PARAMETER_ILLEGAL("300200", "请求参数非法"),
    INVALID_TOKEN_EXCEPTION("300400", "token无效"),
    CALL_API_EXCEPTION("400000", "远程接口调用异常");



    private String code;
    private String message;

    SystemCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
