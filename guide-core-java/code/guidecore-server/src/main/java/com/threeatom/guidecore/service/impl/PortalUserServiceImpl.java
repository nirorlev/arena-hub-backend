package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PortalUser;
import com.threeatom.guidecore.enums.UserOrgRole;
import com.threeatom.guidecore.mapper.PortalUserMapper;
import com.threeatom.guidecore.service.PortalUserService;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PortalUserServiceImpl extends ServiceImpl<PortalUserMapper, PortalUser>
    implements PortalUserService {

    @Override
    public void saveOrUpdate(Integer userId, Integer masterId, UserOrgRole role) {
        PortalUser portalUser = getByUserAndMasterId(userId, masterId);
        if (portalUser == null) {
            save(createPortalUser(userId, masterId, role));
            return;
        }

        updatePortalUser(role, portalUser);
        update(portalUser);
    }

    private void updatePortalUser(UserOrgRole role, PortalUser portalUser) {
        portalUser.setRole(role);
        portalUser.setSyncedDate(OffsetDateTime.now());
    }

    private void update(PortalUser portalUser) {
        QueryWrapper<PortalUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", portalUser.getUserId());
        queryWrapper.eq("master_id", portalUser.getMasterId());
        update(portalUser, queryWrapper);
    }

    @Override
    public PortalUser getByUserAndMasterId(Integer userId, Integer masterId) {
        QueryWrapper<PortalUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        return getOne(queryWrapper);
    }

    private PortalUser createPortalUser(Integer userId, Integer masterId, UserOrgRole role) {
        PortalUser portalUser = new PortalUser();
        portalUser.setUserId(userId);
        portalUser.setMasterId(masterId);
        portalUser.setRole(role);
        return portalUser;
    }
}
