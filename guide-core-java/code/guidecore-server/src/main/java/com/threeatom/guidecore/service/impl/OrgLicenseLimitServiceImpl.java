package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.LicensePermissionsDto;
import com.threeatom.guidecore.entity.OrgLicenseLimit;
import com.threeatom.guidecore.exception.LicenseLimitExceededException;
import com.threeatom.guidecore.mapper.OrgLicenseLimitMapper;
import com.threeatom.guidecore.mapping.UserLicenseMapping;
import com.threeatom.guidecore.service.OrgLicenseLimitService;
import java.util.Optional;
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
    public OrgLicenseLimit getDefaultLicenseLimit(Integer masterId) {
        Optional<OrgLicenseLimit> orgLicenseLimit = getByMasterId(masterId);
        if (orgLicenseLimit.isPresent()) {
            return orgLicenseLimit.get();
        }

        this.save(createNewOrgLimit(masterId));
        sqlSession.flushStatements();
        return getByMasterId(masterId).orElseThrow();
    }

    @Override
    public void checkChannelLimit(Integer orgLicenseId, int expectedChannelCount) {
        OrgLicenseLimit orgLicenseLimit = findById(orgLicenseId);

        if (orgLicenseLimit.getPublishedChannelLimit() < expectedChannelCount) {
            throw new LicenseLimitExceededException("Channel limit exceeded");
        }
    }

    @Override
    public void checkPlaylistLimit(Integer orgLicenseId, int expectedPlaylistCount) {
        OrgLicenseLimit orgLicenseLimit = findById(orgLicenseId);

        if (orgLicenseLimit.getPublishedPlaylistLimit() < expectedPlaylistCount) {
            throw new LicenseLimitExceededException("Playlist limit exceeded");
        }
    }

    @Override
    public LicensePermissionsDto getPermissions(Integer masterId) {
        return userLicenseMapping.map(getByMasterId(masterId).orElseThrow());
    }

    @Transactional(readOnly = true)
    public OrgLicenseLimit findById(Integer orgLicenseId) {
        OrgLicenseLimit orgLicenseLimit = this.getById(orgLicenseId);
        if (orgLicenseLimit == null) {
            throw new IllegalArgumentException(String.format("Org license limit with id %s not found", orgLicenseId));
        }

        return orgLicenseLimit;
    }

    @Transactional(readOnly = true)
    public Optional<OrgLicenseLimit> getByMasterId(Integer masterId) {
        QueryWrapper<OrgLicenseLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master_id", masterId);
        return Optional.ofNullable(this.getOne(queryWrapper));
    }

    private OrgLicenseLimit createNewOrgLimit(Integer masterId) {
        OrgLicenseLimit orgLicenseLimit = new OrgLicenseLimit();
        orgLicenseLimit.setMasterId(masterId);
        orgLicenseLimit.setPublishedChannelLimit(publishChannelLimit);
        orgLicenseLimit.setPublishedPlaylistLimit(publishPlaylistLimit);
        return orgLicenseLimit;
    }
}
