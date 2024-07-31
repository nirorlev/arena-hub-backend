package com.threeatom.guidecore.controller.user.vo;

import java.util.List;
import lombok.Data;

@Data
public class Permissions {

    private List<Groups> groups;

    private Org org;

    private List<Groups> managed_groups;
}
