package com.nexus.framework.aspect;


import com.nexus.common.core.view.Result;
import com.nexus.common.enums.HttpCodeEnum;
import com.nexus.common.utils.ObjectUtils;
import com.nexus.common.utils.RedisUtils;
import com.nexus.common.annotation.CacheRemove;
import com.nexus.common.enums.CacheKeyEnum;
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
