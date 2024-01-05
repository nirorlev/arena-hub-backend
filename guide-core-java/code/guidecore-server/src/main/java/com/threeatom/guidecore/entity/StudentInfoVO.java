package com.threeatom.guidecore.entity;

import lombok.Data;

/**
 * @author Administrator
 * @title: StudentInfoVO
 * @projectName spring-paypal-example
 * @description: TODO
 * @date 2021/12/10/01014:29
 */
@Data
public class StudentInfoVO {
    private Integer userId;

    private Integer videoNum;

    private Integer subTime;

    private Integer permissionId;

    private Integer taskNum;

    private Integer userAccessId;

    public StudentInfoVO(){

    }
    public StudentInfoVO(Integer userAccessId,Integer userId){
        this.userAccessId = userAccessId;
        this.userId = userId;
    }
}
