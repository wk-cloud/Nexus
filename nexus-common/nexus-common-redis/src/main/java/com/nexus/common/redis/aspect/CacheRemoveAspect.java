package com.nexus.common.redis.aspect;


import com.nexus.common.redis.annotation.CacheRemove;
import com.nexus.common.core.domain.view.Result;
import com.nexus.common.core.enums.CacheKeyEnum;

import com.nexus.common.core.enums.HttpCodeEnum;
import com.nexus.common.core.utils.ObjectUtils;

import com.nexus.common.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 缓存删除切面
 *
 * @author wk
 * @date 2024/08/24
 */
@Slf4j
@Component
@Aspect
public class CacheRemoveAspect {

    @Around("@annotation(cacheRemove)")
    public Object cacheRemove(ProceedingJoinPoint proceedingJoinPoint, CacheRemove cacheRemove) throws Throwable {

        Result proceed = (Result) proceedingJoinPoint.proceed();

        if(HttpCodeEnum.SUCCESS.getCode().equals(proceed.getCode())){
            String cacheKey;
            CacheKeyEnum cacheName = cacheRemove.cacheName();
            if(ObjectUtils.isNotNull(cacheName)){
                cacheKey = cacheName.getKey();
            }else {
                cacheKey = cacheRemove.key();
            }
            RedisUtils.delete(cacheKey);
        }


        return proceed;
    }

}
