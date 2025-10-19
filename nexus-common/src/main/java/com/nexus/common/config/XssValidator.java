package com.nexus.common.config;

import com.nexus.common.annotation.Xss;
import com.nexus.common.utils.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * XSS 验证器
 *
 * @author wk
 * @date 2025/02/13
 */
public class XssValidator implements ConstraintValidator<Xss,String> {
    /**
     * 脚本匹配正则
     */
    private final String SCRIPT_PATTERN = "<(script|iframe|object|embed|applet|form|input|textarea|style|img|meta|" +
            "link|base|bgsound|frame|frameset|svg|math|canvas|xml|script.*|javascript:|vbscript:)[^>]*>.*</\\1>";

    @Override
    public void initialize(Xss constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.isBlank(value)){
            return true;
        }
        return !value.matches(SCRIPT_PATTERN);
    }
}
