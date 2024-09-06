package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.mapper.OrgLicenseLimitMapper;
import com.threeatom.guidecore.mapping.UserLicenseMapping;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrgLicenseLimitServiceImpl extends ServiceImpl<OrgLicenseLimitMapper, OrgLicenseLimit>
    implements OrgLicenseLimitService {

    private final UserLicenseMapping userLicenseMapping;
    private final SqlSession sqlSession;

    @Value("${publish.channel.limit.default}")
    private int publishChannelLimit;
    @Value("${publish.playlist.limit.default}")
    private int publishPlaylistLimit;

    @Override
    public void save(Integer masterId) {
        OrgLicenseLimit orgLicenseLimit = getByMasterId(masterId);
        if (orgLicenseLimit != null) {
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
    public OrgLicenseLimit getByMasterId(Integer masterId) {
        QueryWrapper<OrgLicenseLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        return this.getOne(queryWrapper);
    }

    private OrgLicenseLimit createNewOrgLimit(Integer masterId) {
        OrgLicenseLimit orgLicenseLimit = new OrgLicenseLimit();
        orgLicenseLimit.setMasterId(masterId);
        orgLicenseLimit.setPublishedChannelLimit(publishChannelLimit);
        orgLicenseLimit.setPublishedPlaylistLimit(publishPlaylistLimit);
        return orgLicenseLimit;
    }
}
