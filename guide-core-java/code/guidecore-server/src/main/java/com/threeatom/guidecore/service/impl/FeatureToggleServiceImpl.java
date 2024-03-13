package com.threeatom.guidecore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threeatom.guidecore.dto.response.FeatureToggleDto;
import com.threeatom.guidecore.entity.FeatureToggle;
import com.threeatom.guidecore.mapper.FeatureToggleMapper;
import com.threeatom.guidecore.service.FeatureToggleService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeatureToggleServiceImpl extends ServiceImpl<FeatureToggleMapper, FeatureToggle>
    implements FeatureToggleService {

    @Override
    @Transactional(readOnly = true)
    public FeatureToggleDto getAllFeatures() {
        return createFeatureToggleDto(list());
    }

    private FeatureToggleDto createFeatureToggleDto(List<FeatureToggle> featureToggles) {
        FeatureToggleDto featureToggleDto = new FeatureToggleDto();

        Map<String, String> features = featureToggles.stream()
            .collect(Collectors.toMap(FeatureToggle::getName, FeatureToggle::getValue));
        featureToggleDto.setFeatures(features);

        return featureToggleDto;
    }
}
