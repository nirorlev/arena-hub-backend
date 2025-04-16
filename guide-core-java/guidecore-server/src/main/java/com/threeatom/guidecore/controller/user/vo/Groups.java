package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

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
