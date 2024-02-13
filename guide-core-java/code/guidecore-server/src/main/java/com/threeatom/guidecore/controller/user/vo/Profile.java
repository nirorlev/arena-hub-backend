package com.threeatom.guidecore.controller.user.vo;

import lombok.Data;

/**
 * @author Administrator
 * @title: Profile
 * @projectName jeeplus
 * @description: TODO
 * @date 2023/2/28/02811:07
 */
@Data
public class Profile {

    private Integer id;

    private String firstName;

    private String lastName;

    private String thumbUrl;

    private String email;
}
