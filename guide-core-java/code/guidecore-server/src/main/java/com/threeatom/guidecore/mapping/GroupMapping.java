package com.threeatom.guidecore.mapping;

import com.threeatom.client.dto.GroupDto;
import com.threeatom.client.dto.ManagedGroupDto;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.UserGroup;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.enums.UserGroupRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface GroupMapping {

    @Mapping(target = "powtoonGroupCode", source = "powtoonGroup.id")
    @Mapping(target = "name", source = "powtoonGroup.title")
    @Mapping(target = "powtoonParentGroupCode", source = "powtoonGroup.parent_group_id")
    Group map(Groups powtoonGroup, Integer masterId);

    @Mapping(target = "powtoonGroupCode", source = "powtoonUserGroup.id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "role", source = "powtoonUserGroup.roleId", qualifiedByName = "mapRole")
    UserGroup mapUserGroup(GroupDto powtoonUserGroup, Integer userId);

    @Mapping(target = "powtoonGroupCode", source = "powtoonUserManagedGroup.id")
    @Mapping(target = "userId", source = "userId")
    UserManagedGroup mapUserManagedGroup(ManagedGroupDto powtoonUserManagedGroup, Integer userId);

    @Named("mapRole")
    default UserGroupRole mapRole(String roleId) {
        return UserGroupRole.fromRoleId(roleId);
    }
}
