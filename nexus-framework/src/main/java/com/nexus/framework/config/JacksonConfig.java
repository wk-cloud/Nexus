package com.nexus.framework.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * jackson 配置类
 *
 * @author wk
 * @date 2024/01/11
 */
@Slf4j
@Configuration
public class JacksonConfig {

    /**
     * Jackson 对象映射器
     * 处理主键id精度缺失问题
     * true：生效
     * false：失效
     * @param builder
     * @return {@link ObjectMapper}
     */
    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    @ConditionalOnExpression("false")
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder){
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        log.info("====> 序列化配置完成");
        return objectMapper;
    }

}
