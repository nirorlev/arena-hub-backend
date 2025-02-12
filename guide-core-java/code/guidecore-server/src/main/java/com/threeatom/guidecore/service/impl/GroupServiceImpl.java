package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.controller.user.vo.Groups;
import com.threeatom.guidecore.entity.Group;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.mapper.GroupMapper;
import com.threeatom.guidecore.mapping.GroupMapping;
import com.threeatom.guidecore.service.GroupService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final GroupMapping groupMapping;

    @Override
    @Transactional
    public void syncGroups(List<Groups> powtoonGroups, Integer userId, Integer masterId) {
        List<Group> groups = powtoonGroups.stream()
            .map(powtoonGroup -> groupMapping.map(powtoonGroup, masterId))
            .collect(Collectors.toList());

        saveOrUpdateBatch(groups);
    }

    @Override
    public List<Group> findGroups(PortalUser portalUser) {
        return baseMapper.findGroups(portalUser.getUserId(), portalUser.getMasterId());
    }
}
