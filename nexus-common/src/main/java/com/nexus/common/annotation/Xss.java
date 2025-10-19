package com.nexus.common.annotation;


import com.nexus.common.config.XssValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * XSS 校验注解
 *
 * @author wk
 * @date 2025/02/13
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Constraint(validatedBy = { XssValidator.class })
public @interface Xss {
    String message() default "禁止脚本输入";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
