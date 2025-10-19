package com.nexus.common.annotation;



import com.nexus.common.enums.LimitTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limit {
    /**
     * 名字
     */
    String name() default "";

    /**
     * key
     */
    String key() default "";

    /**
     * Key的前缀
     */
    String prefix() default "";


    /**
     * key的后缀
     */
    String suffix() default "";

    /**
     * 给定的时间范围 单位(秒)
     */
    int period() default 10;

    /**
     * 给定时间内最多访问次数
     */
    int count() default 10;

    /**
     * 限流的类型(用户自定义key 或者 请求ip)
     */
    LimitTypeEnum limitType() default LimitTypeEnum.DEFAULT;
}
