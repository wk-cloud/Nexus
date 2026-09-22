package com.nexus.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * redis配置
 *
 * @author wk
 * @date 2022/7/23
 */
@EnableCaching // 开启缓存
@Configuration
public class RedisConfig {

    /**
     * Redis模板
     *
     * @param redisConnectionFactory redis连接工厂
     * @return {@link RedisTemplate }<{@link String }, {@link Object }>
     */
    @ConditionalOnMissingBean(name = "redisTemplate")
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 1. 创建 RedisTemplate 对象
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 2. 设置连接池工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * redis模板定制器
     *
     * @return {@link BeanPostProcessor }
     */
    @ConditionalOnMissingBean(name = "redisTemplateCustomizer")
    @Bean
    public static BeanPostProcessor redisTemplateCustomizer() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) {
                if ("redisTemplate".equals(beanName) && bean instanceof RedisTemplate<?, ?> redisTemplate) {
                    handleRedisTemplate(redisTemplate);
                }
                return bean;
            }
        };
    }

    /**
     * 处理redis模板
     *
     * @param redisTemplate Redis模板
     */
    private static void handleRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
        // 1. 创建对象映射器
        ObjectMapper objectMapper = createObjectMapper();
        // 2. 创建 JSON 序列化工具
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        // 3. 创建字符串序列化工具
        RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();
        // 4. 设置 key 序列化器
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 5. 设置 value 序列化器
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 6. 设置 hash 值序列化器
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
    }

    /**
     * 缓存管理器
     *
     * @param redisConnectionFactory redis连接工厂
     * @return {@link CacheManager }
     */
    @ConditionalOnMissingBean(name = "redisCacheManager")
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = createObjectMapper();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * 创建对象映射器
     *
     * @return {@link ObjectMapper }
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 将当前对象的数据类型也存入序列化的结果字符串中
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        //解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
