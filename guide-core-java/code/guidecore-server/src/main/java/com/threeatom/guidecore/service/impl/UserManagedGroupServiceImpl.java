package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.entity.UserManagedGroup;
import com.threeatom.guidecore.mapper.UserManagedGroupMapper;
import com.threeatom.guidecore.service.UserManagedGroupService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagedGroupServiceImpl extends ServiceImpl<UserManagedGroupMapper, UserManagedGroup>
    implements UserManagedGroupService {

    @Override
    @Transactional(readOnly = true)
    public List<UserManagedGroup> findUserManagedGroups(PortalUser portalUser) {
        return baseMapper.findUserManagedGroups(portalUser.getUserId(), portalUser.getMasterId());
    }
}
