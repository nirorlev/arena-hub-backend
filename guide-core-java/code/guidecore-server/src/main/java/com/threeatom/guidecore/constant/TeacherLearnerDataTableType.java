package com.threeatom.guidecore.constant;

import java.util.List;

public enum TeacherLearnerDataTableType {
    VIDEOS_WATCHED(1, "学生观看视频数量"),
    UPLOADS(2, "上传文件数量"),
    MESSAGES(3, "发布的消息数量"),
    TIME_SPENT_ON_VIDEOS(4, "视频播放的时长"),
    COURSE_NAME(5, "课程名称"),
    LAST_LOG_IN(6, "最后一次登录");

    private int code;
    private String desc;

    TeacherLearnerDataTableType(int code, String desc) {
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
    public static TeacherLearnerDataTableType getByCode(int code) {
        for (TeacherLearnerDataTableType enumType : values()) {
            if (enumType.getCode() == code) {
                return enumType;
            }
        }
        return null;
    }

    /***
     * 判断code值是否都是enum中的code
     * @param list
     * @return
     */
    public static boolean isValidEnum(List<Integer> list) {
        for (Integer val : list) {
            if (!isValidEnum(val)) return false;
        }
        return true;
    }

    public static boolean isValidEnum(Integer code) {
        for (TeacherLearnerDataTableType enumType : values()) {
            if (enumType.getCode() == code) {
                return true;
            }
        }
        return false;
    }
}
