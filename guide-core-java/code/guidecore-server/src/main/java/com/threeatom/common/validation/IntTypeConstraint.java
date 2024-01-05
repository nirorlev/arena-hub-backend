package com.threeatom.common.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 整数类型验证器注解（验证整数的枚举值）
 * @date 2021/3/25 10:48
 * @author wei.heng
 */
@Target( {ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IntTypeValidator.class)
@Documented
public @interface IntTypeConstraint {

	/** 异常提示信息 */
	String message() default "类型异常";

	/** 校验值 */
	int[] values() default {};

	/** 默认配置 - 不加要报错 */
	Class<?>[] groups() default {};
	
	/** 默认配置 - 不加要报错 */
	Class<? extends Payload>[] payload() default {};
}
