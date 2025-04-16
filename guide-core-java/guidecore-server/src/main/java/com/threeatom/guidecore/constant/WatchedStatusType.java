package com.threeatom.guidecore.constant;

public enum WatchedStatusType {
    STARTED(0, "Started"),
    NOTSTARTED(1, "Not Started"),
    COMPLETE(2, "Complete"),
    CH(4, ""),
    NOACCESS(3, "No Access");

    private int code;
    private String desc;

    WatchedStatusType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static WatchedStatusType getByCode(int code) {
        for (WatchedStatusType enumType : values()) {
            if (enumType.getCode() == code) {
                return enumType;
            }
        }
        return null;
    }
}
