package com.threeatom.guidecore.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContentGroupsDto {
    private List<ContentGroupDto> groups;
    private List<ContentGroupDto> managedGroups;
}