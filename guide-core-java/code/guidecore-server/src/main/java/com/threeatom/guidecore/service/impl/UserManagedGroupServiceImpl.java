package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.client.dto.ManagedGroupDto;
import com.threeatom.guidecore.entity.GroupToUserPk;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.mapper.UserManagedGroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.UserManagedGroupService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserManagedGroupServiceImpl extends ServiceImpl<UserManagedGroupMapper, UserManagedGroup>
    implements UserManagedGroupService {

    private final GroupMapping groupMapping;

    @Override
    public UserManagedGroup getById(Serializable id) {
        return baseMapper.getById((GroupToUserPk) id);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<UserManagedGroup> userManagedGroups) {
        if (userManagedGroups.isEmpty()) {
            return false;
        }

        baseMapper.saveOrUpdateBatch(userManagedGroups);
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
    @Transactional(readOnly = true)
    public List<UserManagedGroup> findUserManagedGroups(PortalUser portalUser) {
        return baseMapper.findUserManagedGroups(portalUser.getUserId(), portalUser.getMasterId());
    }

    @Override
    @Transactional
    public void syncUserManagedGroups(List<ManagedGroupDto> powtoonManagedGroups, Integer userId, Integer masterId) {
        List<UserManagedGroup> userManagedGroups = convertToUserManagedGroups(powtoonManagedGroups, userId);
        saveOrUpdateBatch(userManagedGroups);

        List<String> userManagedGroupCodesToRemove = getUserManagedGroupCodesToRemove(powtoonManagedGroups, userId, masterId);
        removeByIds(userManagedGroupCodesToRemove);
    }

    private List<String> getUserManagedGroupCodesToRemove(List<ManagedGroupDto> powtoonManagedGroups, Integer userId, Integer masterId) {
        List<String> powtoonGroupCodes = convertToGroupCodes(powtoonManagedGroups);
        List<UserManagedGroup> existingUserManagedGroups = findByUserAndMasterId(userId, masterId);

        return existingUserManagedGroups.stream()
            .map(userManagedGroup -> userManagedGroup.getId().getPowtoonGroupCode())
            .filter(powtoonGroupCode -> !powtoonGroupCodes.contains(powtoonGroupCode))
            .collect(Collectors.toList());
    }

    private List<UserManagedGroup> convertToUserManagedGroups(List<ManagedGroupDto> powtoonManagedGroups,
                                                              Integer userId) {
        return powtoonManagedGroups.stream()
            .map(powtoonUserGroup -> groupMapping.mapUserManagedGroup(powtoonUserGroup, userId))
            .collect(Collectors.toList());
    }

    private List<String> convertToGroupCodes(List<ManagedGroupDto> powtoonUserManagedGroups) {
        return powtoonUserManagedGroups.stream()
            .map(ManagedGroupDto::getId)
            .collect(Collectors.toList());
    }

    private List<UserManagedGroup> findByUserAndMasterId(Integer userId, Integer masterId) {
        return baseMapper.findUserManagedGroups(userId, masterId);
    }
}
