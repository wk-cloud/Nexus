package com.nexus.common.annotation;



import com.nexus.common.enums.OperationTypeEnum;

import java.lang.annotation.*;


/**
 * 操作日志注解
 *
 * @author wk
 * @date 2022/11/5
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OperationLog {

    /**
     * 模块名
     *
     * @return {@link String}
     */
    String module() default "";

    /**
     * 操作类型
     *
     * @return {@link OperationTypeEnum}
     */
    OperationTypeEnum operationType() default OperationTypeEnum.OTHER;

    /**
     * 操作描述
     *
     * @return {@link String}
     */
    String operationDesc() default "";

    /**
     * 请求类型
     *
     * @return {@link String}
     */
    String requestType() default "";

    /**
     * 请求 URL
     *
     * @return {@link String}
     */
    String requestUrl() default "";
}
