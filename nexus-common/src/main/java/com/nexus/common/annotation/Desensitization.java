package com.nexus.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nexus.common.enums.DesensitizationTypeEnum;
import com.nexus.common.config.DesensitizationJsonSerializable;

import java.lang.annotation.*;

/**
 * 数据脱敏
 *
 * @author wk
 * @date 2024/12/29
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizationJsonSerializable.class)
public @interface Desensitization {

    /**
     * 脱敏类型
     *
     * @return {@link DesensitizationTypeEnum}
     */
    DesensitizationTypeEnum desensitizationType();
}
