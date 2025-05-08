package com.threeatom.guidecore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@Data
@TableName(value = "course_content", autoResultMap = true)
public class CourseContent implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer courseId;
    private Integer contentId;

    @TableField(value = "\"order\"")
    private Integer order;

    @TableField(exist = false)
    private Course course;

    @TableField(exist = false)
    private GcVideo video;
}
