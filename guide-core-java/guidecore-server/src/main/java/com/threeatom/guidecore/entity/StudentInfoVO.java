package com.threeatom.guidecore.entity;

import lombok.Data;

@Data
public class StudentInfoVO {
    private Integer userId;

    private Integer videoNum;

    private Integer subTime;

    private Integer permissionId;

    private Integer taskNum;

    private Integer userAccessId;

    public StudentInfoVO() {}

    public StudentInfoVO(Integer userAccessId, Integer userId) {
        this.userAccessId = userAccessId;
        this.userId = userId;
    }
}
