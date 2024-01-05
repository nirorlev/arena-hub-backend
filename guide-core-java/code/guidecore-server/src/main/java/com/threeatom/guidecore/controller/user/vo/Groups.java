package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author Administrator
 * @title: Groups
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/2/28/02810:49
 */
@Data
public class Groups {

    private String id;

    private String title;

    private String role_id;

    private String parent_group_id;

    private String creation_date;

    private String created_by;

    private Integer user_count;

    private String thumb_url;
}
