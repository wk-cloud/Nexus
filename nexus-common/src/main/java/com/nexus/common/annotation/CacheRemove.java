package com.nexus.common.annotation;



import com.nexus.common.enums.CacheKeyEnum;

import java.lang.annotation.*;

/**
 * 缓存
 *
 * @author wk
 * @date 2024/08/24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheRemove {

    String key() default "";
    CacheKeyEnum cacheName();
}
