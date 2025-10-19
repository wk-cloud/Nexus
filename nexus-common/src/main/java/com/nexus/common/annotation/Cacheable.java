package com.nexus.common.annotation;



import com.nexus.common.enums.CacheKeyEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

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
public @interface Cacheable {

    String key() default "";
    CacheKeyEnum cacheName();
    int expired() default 0;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
