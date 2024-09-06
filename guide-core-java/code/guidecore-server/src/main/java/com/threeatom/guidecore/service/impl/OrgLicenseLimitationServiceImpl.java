package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimitation;
import com.threeatom.guidecore.mapper.OrgLicenseLimitationMapper;
import com.threeatom.guidecore.mapping.UserLicenseMapping;
import com.threeatom.guidecore.service.OrgLicenseLimitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrgLicenseLimitationServiceImpl extends ServiceImpl<OrgLicenseLimitationMapper, OrgLicenseLimitation>
    implements OrgLicenseLimitationService {

    private final UserLicenseMapping userLicenseMapping;

    private static final int PUBLISH_CHANNEL_LIMIT_DEFAULT = 1;
    private static final int PUBLISH_PLAYLIST_LIMIT_DEFAULT = 1;

    @Override
    public void save(Integer masterId) {
        OrgLicenseLimitation orgLicenseLimitation = getByMasterId(masterId);
        if (orgLicenseLimitation != null) {
            return;
        }

        this.save(createNewOrgLimit(masterId));
    }

    @Override
    public LicensePermissionsDto getPermissions(Integer masterId) {
        return userLicenseMapping.map(getByMasterId(masterId));
    }

    @Transactional(readOnly = true)
    @Override
    public OrgLicenseLimitation getByMasterId(Integer masterId) {
        QueryWrapper<OrgLicenseLimitation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        return this.getOne(queryWrapper);
    }

    private OrgLicenseLimitation createNewOrgLimit(Integer masterId) {
        OrgLicenseLimitation orgLicenseLimitation = new OrgLicenseLimitation();
        orgLicenseLimitation.setMasterId(masterId);
        orgLicenseLimitation.setPublishedChannelLimit(PUBLISH_CHANNEL_LIMIT_DEFAULT);
        orgLicenseLimitation.setPublishedPlaylistLimit(PUBLISH_PLAYLIST_LIMIT_DEFAULT);
        return orgLicenseLimitation;
    }
}
