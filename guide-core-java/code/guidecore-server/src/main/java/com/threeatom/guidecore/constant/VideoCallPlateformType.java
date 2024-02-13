package com.threeatom.guidecore.constant;

public enum VideoCallPlateformType {
    ZOOM(1, "ZOOM");

    private int code;
    private String desc;

    VideoCallPlateformType(int code, String desc) {
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

    /***
     * 根据CODE获取枚举实例
     * @param code
     * @return
     */
    public static VideoCallPlateformType getByCode(int code) {
        for (VideoCallPlateformType enumType : values()) {
            if (enumType.getCode() == code) {
                return enumType;
            }
        }
        return null;
    }
}
