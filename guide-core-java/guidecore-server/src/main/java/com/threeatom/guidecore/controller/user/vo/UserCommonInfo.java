package com.threeatom.guidecore.controller.user.vo;

import com.threeatom.system.entity.SysFile;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class UserCommonInfo {

    private static final long serialVersionUID = 1L;

    private Integer userId;
    private Integer masterId;

    private Integer groupId;

    private Integer userAccessId;

    private String username;

    private String lastName;

    private String firstName;

    private SysFile avatarFile;

    private Date lastLogin;

    private List<Integer> userIds;

    private Integer taskNum;

    private String name;

    private Integer accessId;
}
