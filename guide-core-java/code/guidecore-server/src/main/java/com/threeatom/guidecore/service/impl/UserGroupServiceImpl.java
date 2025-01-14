package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.dto.GroupDto;
import com.threeatom.guidecore.entity.GroupToUserPk;
import com.threeatom.guidecore.entity.UserGroup;
import com.threeatom.guidecore.mapper.UserGroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.UserGroupService;
import java.io.Serializable;
import java.util.Collection;
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
    public UserGroup getById(Serializable id) {
        return baseMapper.getById((GroupToUserPk) id);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<UserGroup> userGroups) {
        if (userGroups.isEmpty()) {
            return false;
        }

        baseMapper.saveOrUpdateBatch(userGroups);
        return true;
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        if (ids.isEmpty()) {
            return false;
        }

        return baseMapper.removeByIds(ids);
    }

    @Override
    @Transactional
    public void syncUserGroups(List<GroupDto> powtoonUserMemberGroups, Integer userId, Integer masterId) {
        List<UserGroup> userGroups = convertToUserGroups(powtoonUserMemberGroups, userId);
        saveOrUpdateBatch(userGroups);

        List<GroupToUserPk> userGroupIdsToRemove = getUserGroupCodesToRemove(powtoonUserMemberGroups, userId, masterId);
        removeByIds(userGroupIdsToRemove);
    }

    private List<GroupToUserPk> getUserGroupCodesToRemove(List<GroupDto> powtoonMemberGroups, Integer userId,
                                                          Integer masterId) {
        List<String> powtoonGroupCodes = convertToGroupCodes(powtoonMemberGroups);
        List<UserGroup> existingUserGroups = findByUserAndMasterId(userId, masterId);

        return existingUserGroups.stream()
            .map(UserGroup::getId)
            .filter(groupToUserPk -> !powtoonGroupCodes.contains(groupToUserPk.getPowtoonGroupCode()))
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

    private List<UserGroup> findByUserAndMasterId(Integer userId, Integer masterId) {
        return baseMapper.findByUserAndMasterId(userId, masterId);
    }
}
