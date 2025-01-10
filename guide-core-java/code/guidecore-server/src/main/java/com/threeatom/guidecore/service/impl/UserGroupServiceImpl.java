package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.dto.GroupDto;
import com.threeatom.guidecore.entity.UserGroup;
import com.threeatom.guidecore.mapper.UserGroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.UserGroupService;
import java.util.List;
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
    public void syncUserGroups(List<GroupDto> powtoonUserMemberGroups, Integer userId) {
        List<UserGroup> userGroups = convertToUserGroups(powtoonUserMemberGroups, userId);
        saveOrUpdateBatch(userGroups);

        List<String> userGroupCodesToRemove = getUserGroupCodesToRemove(powtoonUserMemberGroups, userId);
        removeByIds(userGroupCodesToRemove);
    }

    private List<String> getUserGroupCodesToRemove(List<GroupDto> powtoonMemberGroups, Integer userId) {
        List<String> powtoonGroupCodes = convertToGroupCodes(powtoonMemberGroups);
        List<UserGroup> existingUserGroups = findByUserId(userId);

        return existingUserGroups.stream()
            .map(UserGroup::getPowtoonGroupCode)
            .filter(powtoonGroupCode -> !powtoonGroupCodes.contains(powtoonGroupCode))
            .collect(Collectors.toList());
    }

    private List<UserGroup> convertToUserGroups(List<GroupDto> powtoonMemberGroups, Integer userId) {
        return powtoonMemberGroups.stream()
            .map(powtoonUserGroup -> groupMapping.mapUserGroup(powtoonUserGroup, userId))
            .collect(Collectors.toList());
    }

    private List<String> convertToGroupCodes(List<GroupDto> powtoonUserGroups) {
        return powtoonUserGroups.stream()
            .map(GroupDto::getId)
            .collect(Collectors.toList());
    }

    private List<UserGroup> findByUserId(Integer userId) {
        QueryWrapper<UserGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return list(queryWrapper);
    }
}
