package com.nexus.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.nexus.common.annotation.Desensitization;
import com.nexus.common.enums.DesensitizationTypeEnum;

import java.io.IOException;
import java.util.Objects;


/**
 * 脱敏 JSON 可序列化
 *
 * @author wk
 * @date 2024/12/29
 */
public class DesensitizationJsonSerializable extends JsonSerializer<String> implements ContextualSerializer {
    private DesensitizationTypeEnum desensitizationTypeEnum;

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 将字符串按照设定的脱敏策略进行脱敏处理后序列化到 JSON 中
        jsonGenerator.writeString(desensitizationTypeEnum.getDesensitization().apply(s));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 获取属性上的 Desensitization 注解
        Desensitization annotation = beanProperty.getAnnotation(Desensitization.class);

        // 判断注解不为空且属性类型为 String
        if (Objects.nonNull(annotation) && Objects.equals(String.class, beanProperty.getType().getRawClass())) {
            // 设置脱敏策略
            this.desensitizationTypeEnum = annotation.desensitizationType();
            return this;
        }

        // 返回默认的序列化器
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}
