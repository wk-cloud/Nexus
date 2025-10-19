package com.nexus.common.annotation;

import java.lang.annotation.*;

/**
 * XSS 过滤注解
 *
 * @author wk
 * @date 2025/02/12
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface XssFilter {
    /**
     * 是否开启
     *
     * @return boolean
     */
    boolean enable() default true;
}
