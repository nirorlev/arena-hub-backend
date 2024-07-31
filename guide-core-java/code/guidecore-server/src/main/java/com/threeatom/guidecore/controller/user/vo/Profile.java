package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

@Data
public class Profile {

    private Integer id;

    private String firstName;

    private String lastName;

    private String thumbUrl;

    private String email;
}
