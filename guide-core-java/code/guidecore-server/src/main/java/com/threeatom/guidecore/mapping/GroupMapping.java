package com.threeatom.guidecore.mapping;

import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.UserGroup;
import com.threeatom.guidecore.enums.UserGroupRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper
public interface GroupMapping {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "powtoonGroup.title")
    @Mapping(target = "powtoonGroupId", source = "powtoonGroup.id")
    @Mapping(target = "parentCode", source = "powtoonGroup.parent_group_id")
    @Mapping(target = "masterId", source = "masterId")
    Group map(Groups powtoonGroup, Integer masterId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "title")
    @Mapping(target = "parentCode", source = "parent_group_id")
    Group update(@MappingTarget Group group, Groups powtoonGroup);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "groupId", source = "groupId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRole")
    UserGroup mapUserGroup(String roleId, Integer groupId, Integer userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRole")
    UserGroup updateUserGroup(@MappingTarget UserGroup userGroup, String roleId);

    @Named("mapRole")
    default UserGroupRole mapRole(String roleId) {
        return UserGroupRole.fromRoleId(roleId);
    }
}
