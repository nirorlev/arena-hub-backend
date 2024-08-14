package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.common.exception.ResourceNotFoundException;
import com.threeatom.guidecore.dto.FeatureToggleValueDto;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.FeatureToggle;
import com.threeatom.guidecore.mapper.FeatureToggleMapper;
import com.threeatom.guidecore.mapping.FeatureToggleMapping;
import com.threeatom.guidecore.service.FeatureToggleService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FeatureToggleServiceImpl extends ServiceImpl<FeatureToggleMapper, FeatureToggle>
    implements FeatureToggleService {

    private static final String FEATURE_NAME_COLUMN = "name";
    private static final String MASTER_ID_COLUMN = "master_id";

    private final FeatureToggleMapping featureToggleMapping;

    @Override
    @Transactional(readOnly = true)
    public FeatureToggleDto getAllDefaultFeatures() {
        QueryWrapper<FeatureToggle> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull(MASTER_ID_COLUMN);

        return createFeatureToggleDto(list(queryWrapper));
    }

    @Override
    @Transactional(readOnly = true)
    public FeatureToggleDto getAllFeatureToggles(Integer masterId) {
        return createFeatureToggleDto(getFeatureTogglesForMasterId(masterId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeatureToggleValueDto> getAllFeatureToggles() {
        return list().stream()
            .map(featureToggleMapping::map)
            .collect(Collectors.toList());
    }

    @Override
    public FeatureToggleValueDto getFeatureToggle(String featureName) {
        return featureToggleMapping.map(getByName(featureName));
    }

    private FeatureToggle getByName(String featureName) {
        QueryWrapper<FeatureToggle> queryWrapper = new QueryWrapper<FeatureToggle>()
            .eq(FEATURE_NAME_COLUMN, featureName);
        FeatureToggle featureToggle = getOne(queryWrapper);

        if (featureToggle == null) {
            throw new ResourceNotFoundException("Feature toggle not found: " + featureName);
        }

        return featureToggle;
    }

    @Override
    public FeatureToggleValueDto getFeatureToggle(String featureName, Integer masterId) {
        if (masterId == null) {
            return getFeatureToggle(featureName);
        }

        FeatureToggle featureToggle = getByNameAndMasterId(featureName, masterId);
        if (featureToggle == null) {
            return getFeatureToggle(featureName);
        }

        return featureToggleMapping.map(featureToggle);
    }

    private FeatureToggle getByNameAndMasterId(String featureName, Integer masterId) {
        QueryWrapper<FeatureToggle> queryWrapper = new QueryWrapper<FeatureToggle>()
            .eq(FEATURE_NAME_COLUMN, featureName)
            .eq(MASTER_ID_COLUMN, masterId);
        return getOne(queryWrapper);
    }

    @Override
    public void updateFeatureToggle(FeatureToggleValueDto featureToggleValueDto) {
        if (featureToggleValueDto.getMasterId() == null) {
            FeatureToggle featureToggle = getByName(featureToggleValueDto.getName());
            featureToggle.setValue(featureToggleValueDto.getValue());
            updateById(featureToggle);
            return;
        }

        FeatureToggle featureToggleByMaterId =
            getByNameAndMasterId(featureToggleValueDto.getName(), featureToggleValueDto.getMasterId());
        if (featureToggleByMaterId == null) {
            throw new ResourceNotFoundException(
                String.format("Feature toggle not found: %s. Master id: %s", featureToggleValueDto.getName(),
                    featureToggleValueDto.getMasterId()));
        }

        featureToggleByMaterId.setValue(featureToggleValueDto.getValue());
        updateById(featureToggleByMaterId);
    }

    private List<FeatureToggle> getFeatureTogglesForMasterId(Integer masterId) {
        QueryWrapper<FeatureToggle> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull(MASTER_ID_COLUMN)
            .or()
            .eq(MASTER_ID_COLUMN, masterId);

        return filterMasterLevel(this.baseMapper.selectList(queryWrapper), masterId);
    }

    private List<FeatureToggle> filterMasterLevel(List<FeatureToggle> featureToggles, Integer masterId) {
        List<String> featureNames = getFeatureNames(featureToggles);

        return featureNames.stream()
            .map(featureName -> {
                Optional<FeatureToggle> defaultFeaturedToggle = getDefaultFeatureToggle(featureToggles, featureName);
                Optional<FeatureToggle> masterFeaturedToggle =
                    getMasterFeatureToggle(featureToggles, masterId, featureName);

                return defaultFeaturedToggle.map(masterFeaturedToggle::orElse).orElse(null);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<String> getFeatureNames(List<FeatureToggle> featureToggles) {
        return featureToggles.stream()
            .map(FeatureToggle::getName)
            .distinct()
            .collect(Collectors.toList());
    }

    private Optional<FeatureToggle> getMasterFeatureToggle(List<FeatureToggle> featureToggles, Integer masterId,
                                                           String featureName) {
        return featureToggles.stream()
            .filter(featureToggle -> featureToggle.getName().equals(featureName) &&
                Objects.equals(featureToggle.getMasterId(), (masterId)))
            .findFirst();
    }

    private Optional<FeatureToggle> getDefaultFeatureToggle(List<FeatureToggle> featureToggles, String featureName) {
        return featureToggles.stream()
            .filter(
                featureToggle -> featureToggle.getName().equals(featureName) && featureToggle.getMasterId() == null)
            .findFirst();
    }

    private FeatureToggleDto createFeatureToggleDto(List<FeatureToggle> featureToggles) {
        FeatureToggleDto featureToggleDto = new FeatureToggleDto();

        Map<String, String> features = featureToggles.stream()
            .collect(Collectors.toMap(FeatureToggle::getName, FeatureToggle::getValue));
        featureToggleDto.setFeatures(features);

        return featureToggleDto;
    }
}
