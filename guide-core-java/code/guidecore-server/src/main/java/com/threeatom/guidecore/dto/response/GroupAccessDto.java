package com.threeatom.guidecore.dto.response;

import com.threeatom.guidecore.enums.SourceType;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupAccessDto {

    private Boolean isPrivate = false;
    private Boolean isPublic = false;
    private AccessSourceDto source;

    private List<AccessGroupDetailsDto> groups = new ArrayList<>();
}
