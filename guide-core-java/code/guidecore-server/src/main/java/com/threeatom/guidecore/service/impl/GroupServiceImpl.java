package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.mapper.GroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.GroupService;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private GroupMapping groupMapping;

    @Override
    @Transactional
    public List<Group> syncGroups(List<Groups> groups, Integer userId, Integer masterId) {
        List<Group> existingGroups = findByUserAndMasterId(userId, masterId);
        List<String> powtoonGroupIds = getPowtoonGroupIds(groups);

        List<Integer> groupsToDelete = existingGroups.stream()
            .filter(group -> !powtoonGroupIds.contains(group.getPowtoonGroupId()))
            .map(Group::getId)
            .collect(Collectors.toList());

        Map<String, Group> existingGroupsMap = existingGroups.stream()
            .collect(Collectors.toMap(Group::getPowtoonGroupId, Function.identity()));
        List<Group> result = groups.stream()
            .map(group -> convertGroup(existingGroupsMap, group, masterId))
            .collect(Collectors.toList());

        removeByIds(groupsToDelete);
        saveOrUpdateBatch(result);

        return result;
    }

    private Group convertGroup(Map<String, Group> existingGroupsMap, Groups group, Integer masterId) {
        return existingGroupsMap.containsKey(group.getId())
            ? groupMapping.update(existingGroupsMap.get(group.getId()), group)
            : groupMapping.map(group, masterId);
    }

    private List<Group> findByUserAndMasterId(Integer userId, Integer masterId) {
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        return list(queryWrapper);
    }

    private List<String> getPowtoonGroupIds(List<Groups> groups) {
        return groups.stream()
            .map(Groups::getId)
            .collect(Collectors.toList());
    }
}
