package com.threeatom.client.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {
    private List<GroupDto> groups;
    private List<ManagedGroupDto> managedGroups;
    private OrgDto org;
}
