package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.entity.PortalUser;
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
    public void saveOrUpdate(Integer userId, Integer masterId, String role) {
        PortalUser portalUser = getByUserAndMasterId(userId, masterId);
        if (portalUser == null) {
            save(createPortalUser(userId, masterId, role));
            return;
        }

        if (!portalUser.getRole().equals(role)) {
            updatePortalUser(role, portalUser);
            updateById(portalUser);
        }
    }

    private void updatePortalUser(String role, PortalUser portalUser) {
        portalUser.setRole(role);
        portalUser.setModifiedDate(OffsetDateTime.now());
    }

    @Override
    public PortalUser getByUserAndMasterId(Integer userId, Integer masterId) {
        QueryWrapper<PortalUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("master_id", masterId);
        return getOne(queryWrapper);
    }

    private PortalUser createPortalUser(Integer userId, Integer masterId, String role) {
        PortalUser portalUser = new PortalUser();
        portalUser.setUserId(userId);
        portalUser.setMasterId(masterId);
        portalUser.setRole(role);
        return portalUser;
    }
}
