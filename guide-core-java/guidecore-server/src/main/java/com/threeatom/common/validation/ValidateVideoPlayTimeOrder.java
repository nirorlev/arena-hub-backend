package com.threeatom.common.validation;

import com.threeatom.common.validation.annotation.ValidateTimeOrder;
import com.threeatom.guidecore.dto.request.VideoPlayDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidateVideoPlayTimeOrder implements ConstraintValidator<ValidateTimeOrder, VideoPlayDto> {
    @Override
    public boolean isValid(VideoPlayDto videoPlayDto, ConstraintValidatorContext context) {
        if (videoPlayDto == null
            || videoPlayDto.getStartTime() == null
            || videoPlayDto.getEndTime() == null) {
            return false;
        }

        return videoPlayDto.getStartTime() < videoPlayDto.getEndTime();
    }
}