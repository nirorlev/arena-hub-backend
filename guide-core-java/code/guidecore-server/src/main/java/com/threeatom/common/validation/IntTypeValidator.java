package com.threeatom.common.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 类型验证器
 * @date 2021/3/25 10:28
 * @author wei.heng
 */
public class IntTypeValidator implements ConstraintValidator<IntTypeConstraint, Integer> {

    private IntTypeConstraint annotation;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int[] values = annotation.values();
        List<Integer> list = Arrays.stream(values).boxed().collect(Collectors.toList());
        return list.contains(value);
    }

    @Override
    public void initialize(IntTypeConstraint intTypeConstraintAnnotation) {
        this.annotation = intTypeConstraintAnnotation;
    }
}
