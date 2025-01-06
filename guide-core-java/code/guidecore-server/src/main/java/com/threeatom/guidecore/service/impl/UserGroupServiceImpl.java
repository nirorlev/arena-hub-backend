package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.UserGroup;
import com.threeatom.guidecore.mapper.UserGroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.UserGroupService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements UserGroupService {

    private final GroupMapping groupMapping;

    @Override
    @Transactional
    public void syncUserGroups(List<Groups> powtoonGroups, List<Group> arenaGroups, Integer userId) {
        List<UserGroup> existingUserGroups = findByUserId(userId);
        Map<Integer, UserGroup> groupIdToExistingUserGroup = existingUserGroups.stream()
            .collect(Collectors.toMap(UserGroup::getGroupId, Function.identity()));
        Map<String, Groups> groupIdToPowtoonGroup = powtoonGroups.stream()
            .collect(Collectors.toMap(Groups::getId, Function.identity()));

        List<UserGroup> userGroups = arenaGroups.stream()
            .map(arenaGroup -> convertUserGroup(arenaGroup, groupIdToPowtoonGroup, groupIdToExistingUserGroup, userId))
            .collect(Collectors.toList());

        saveOrUpdateBatch(userGroups);
    }

    private UserGroup convertUserGroup(Group arenaGroup, Map<String, Groups> groupIdToPowtoonGroup,
                                       Map<Integer, UserGroup> groupIdToExistingUserGroup, Integer userId) {
        Groups powtoonGroup = groupIdToPowtoonGroup.get(arenaGroup.getPowtoonGroupId());
        String roleId = powtoonGroup.getRole_id();

        return groupIdToExistingUserGroup.containsKey(arenaGroup.getId())
            ? groupMapping.updateUserGroup(groupIdToExistingUserGroup.get(arenaGroup.getId()), roleId)
            : groupMapping.mapUserGroup(roleId, arenaGroup.getId(), userId);
    }

    @Transactional
    public List<UserGroup> findByUserId(Integer userId) {
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }
}
