package com.threeatom.guidecore.dto.response;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupAccessDto {

    @JSONField(name = "private")
    private Boolean isPrivate = false;
    @JSONField(name = "public")
    private Boolean isPublic = false;

    private List<AccessGroupDetailsDto> groups = new ArrayList<>();
}
