package com.nexus.common.annotation;

import java.lang.annotation.*;

/**
 * 接口放行注解
 *
 * @author wk
 * @date 2024/01/02
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Pass {
}
