package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.ValidateTimeOrder;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidateVideoPlayTimeOrder implements ConstraintValidator<ValidateTimeOrder, VideoPlayDto> {
    @Override
    public boolean isValid(VideoPlayDto videoPlayDto, ConstraintValidatorContext context) {
        if (videoPlayDto == null
            || videoPlayDto.getStartWatchTimeInSeconds() == null
            || videoPlayDto.getEndWatchTimeInSeconds() == null) {
            return false;
        }

        return videoPlayDto.getStartWatchTimeInSeconds() < videoPlayDto.getEndWatchTimeInSeconds();
    }
}