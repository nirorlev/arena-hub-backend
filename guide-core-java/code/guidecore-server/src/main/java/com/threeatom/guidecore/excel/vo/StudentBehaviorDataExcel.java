package com.threeatom.guidecore.excel.vo;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.TableField;
import com.threeatom.common.mybatis.typehandler.FastJsonArrayTypeHandler;
import com.threeatom.guidecore.entity.GcSubject;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
public class StudentBehaviorDataExcel {

    private Integer userId;

    private String firstName;

    private String lastName;

    private String userName;

    private Integer watchedNum;

    private Integer allPlayTime;

    private Integer answerNum;

    private List<GcSubject> subjectList;

    @ApiModelProperty(value = "用户权限表")
    @TableField(value = "sub_permission", typeHandler = FastJsonArrayTypeHandler.class)
    private JSONArray subPermission;
}
