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
import com.threeatom.guidecore.service.GcMasterService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FeatureToggleServiceImpl extends ServiceImpl<FeatureToggleMapper, FeatureToggle>
    implements FeatureToggleService {

    private static final String FEATURE_NAME_COLUMN = "name";
    private static final String MASTER_ID_COLUMN = "master_id";

    private final FeatureToggleMapping featureToggleMapping;
    private final GcMasterService masterService;

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
        return featureToggleMapping.map(getDefaultByName(featureName));
    }

    private FeatureToggle getDefaultByName(String featureName) {
        QueryWrapper<FeatureToggle> queryWrapper = new QueryWrapper<FeatureToggle>()
            .eq(FEATURE_NAME_COLUMN, featureName)
            .isNull(MASTER_ID_COLUMN);

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
        FeatureToggle defaultByName = getDefaultByName(featureToggleValueDto.getName());
        if (featureToggleValueDto.getMasterId() == null) {
            defaultByName.setValue(featureToggleValueDto.getValue());
            updateById(defaultByName);
            return;
        }

        FeatureToggle featureToggleByMaterId =
            getByNameAndMasterId(featureToggleValueDto.getName(), featureToggleValueDto.getMasterId());
        if (featureToggleByMaterId == null) {
            createNewPortalLevel(featureToggleValueDto);
            return;
        }

        featureToggleByMaterId.setValue(featureToggleValueDto.getValue());
        updateById(featureToggleByMaterId);
    }

    private void createNewPortalLevel(FeatureToggleValueDto featureToggleValueDto) {
        log.warn("Feature toggle not found: {}. Master id: {}. Creating new one"
            , featureToggleValueDto.getName()
            , featureToggleValueDto.getMasterId()
        );

        if (masterService.getMasterById(featureToggleValueDto.getMasterId()) == null) {
            throw new ResourceNotFoundException(
                String.format("Portal with master id %s not found", featureToggleValueDto.getMasterId()));
        }

        save(featureToggleMapping.map(featureToggleValueDto));
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
