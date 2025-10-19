package com.nexus.framework.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime 序列化配置
 *
 * @author wk
 * @date 2025/03/26
 */
@Configuration
public class LocalDateTimeSerializerConfig {

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String DATE_TIME_PATTERN;

    private final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * localDateTime 序列化器
     *
     * @return {@link LocalDateTimeSerializer}
     */
    @Bean
    public LocalDateTimeSerializer localDateTimeSerializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    /**
     * localDateTime 反序列化器
     *
     * @return {@link LocalDateTimeDeserializer}
     */
    @Bean
    public LocalDateTimeDeserializer localDateTimeDeserializer() {
        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    /**
     * localDate 序列化器
     *
     * @return {@link LocalDateSerializer}
     */
    @Bean
    public LocalDateSerializer localDateSerializer() {
        return new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * localDate 反序列化器
     *
     * @return {@link LocalDateDeserializer}
     */
    @Bean
    public LocalDateDeserializer localDateDeserializer() {
        return new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * Jackson2 Object Mapper Builder 定制器
     *
     * @return {@link Jackson2ObjectMapperBuilderCustomizer}
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, localDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, localDateTimeDeserializer());
            builder.serializerByType(LocalDate.class, localDateSerializer());
            builder.deserializerByType(LocalDate.class, localDateDeserializer());
            builder.simpleDateFormat(DATE_TIME_PATTERN);
        };
    }

}
