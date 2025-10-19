package com.nexus.framework.aspect;


import com.alibaba.fastjson2.JSON;
import com.nexus.common.core.view.Result;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.RedisUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.annotation.Cacheable;
import com.nexus.common.enums.CacheKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 缓存切面
 *
 * @author wk
 * @date 2024/08/24
 */
@Slf4j
@Aspect
@Component
public class CacheableAspect {

    @Around("@annotation(cacheable)")
    public Object cacheable(ProceedingJoinPoint proceedingJoinPoint, Cacheable cacheable) throws Throwable {
        String cacheKey;
        // 获取缓存key
        CacheKeyEnum cacheName = cacheable.cacheName();
        if (ObjectUtils.isNotNull(cacheName)) {
            cacheKey = cacheName.getKey();
        } else {
            cacheKey = cacheable.key();
        }
        // 获取缓存value
        String value = (String) RedisUtils.get(cacheKey);
        if (StringUtils.isBlank(value)) {
            // 环绕后通知，获取响应结果
            Object proceed = proceedingJoinPoint.proceed();
            if (cacheable.expired() > 0) {
                RedisUtils.setEx(cacheKey, JSON.toJSONString(proceed), cacheable.expired(), cacheable.timeUnit());
            } else {
                RedisUtils.set(cacheKey, JSON.toJSONString(proceed));
            }
            return proceed;
        }
        return JSON.parseObject(value, Result.class);
    }

}
