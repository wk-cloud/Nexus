package com.nexus.framework.aspect;

import cn.hutool.crypto.digest.MD5;
import com.nexus.common.exception.LimitAccessException;
import com.nexus.common.utils.IpUtils;
import com.nexus.common.utils.RedisUtils;
import com.nexus.common.utils.StringUtils;
import com.nexus.common.annotation.Limit;
import com.nexus.common.enums.LimitTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 限流切面
 *
 * @author wk
 * @date 2023/3/28
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class LimitAspect {

    @Before("@annotation(limit)")
    public void interceptor(JoinPoint pjp, Limit limit) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        LimitTypeEnum limitType = limit.limitType();
        int limitPeriod = limit.period();
        int limitCount = limit.count();

        // 根据限流类型获取不同的key ,如果不传默认会以 （方法名 + 请求类型 + 请求url)  作为key，如果限流类型是 IP，则在默认 key 的基础上再加上 ip
        String key = switch (limitType) {
            case CUSTOM -> limit.key();
            case IP -> {
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                yield getDefaultKey(method, pjp) + ":" + IpUtils.getIpAddress(request);
            }
            default -> getDefaultKey(method, pjp);
        };

        if (StringUtils.isBlank(key)) {
            key = getDefaultKey(method, pjp);
        }
        if (StringUtils.isNotBlank(limit.prefix())) {
            key = limit.prefix().concat(key);
        }
        if (StringUtils.isNotBlank(limit.suffix())) {
            key = key.concat(limit.suffix());
        }
        MD5 md5 = MD5.create();
        key = md5.digestHex(key);

        List<String> keyList = Collections.singletonList(key);

        String luaScript = buildLuaScript();
        RedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        Long count = RedisUtils.execute(redisScript, keyList, limitCount, limitPeriod);
        if (count.intValue() > limitCount) {
            throw new LimitAccessException("访问频繁，已限制访问");
        }
    }


    /**
     * 构建lua脚本
     * redis Lua 限流脚本
     *
     * @return {@link String}
     */
    public String buildLuaScript() {
        StringBuilder lua = new StringBuilder();
        lua.append("local c");
        lua.append("\nc = redis.call('get',KEYS[1])");
        // 调用不超过最大值，则直接返回
        lua.append("\nif c and tonumber(c) > tonumber(ARGV[1]) then");
        lua.append("\nreturn c;");
        lua.append("\nend");
        // 执行计算器自加
        lua.append("\nc = redis.call('incr',KEYS[1])");
        lua.append("\nif tonumber(c) == 1 then");
        // 从第一次调用开始限流，设置对应键值的过期
        lua.append("\nredis.call('expire',KEYS[1],ARGV[2])");
        lua.append("\nend");
        lua.append("\nreturn c;");
        return lua.toString();
    }

    /**
     * 获取请求url
     *
     * @param method 方法
     * @return {@link String}
     */
    public Map<String, String> getRequestUrlAndMethod(Method method, JoinPoint joinPoint) {
        // 获取基础请求url
        Object target = joinPoint.getTarget();
        RequestMapping requestMapping = target.getClass().getAnnotation(RequestMapping.class);
        String baseUrl = requestMapping.value()[0];

        String url = "";
        String requestMethod = "";
        // 获取请求url
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            requestMethod = RequestMethod.valueOf("GET").name();
            if (getMapping.value().length > 0) {
                url = getMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            requestMethod = RequestMethod.valueOf("POST").name();
            if (postMapping.value().length > 0) {
                url = postMapping.value()[0];
            }

        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            requestMethod = RequestMethod.valueOf("DELETE").name();
            if (deleteMapping.value().length > 0) {
                url = deleteMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            requestMethod = RequestMethod.valueOf("PUT").name();
            if (putMapping.value().length > 0) {
                url = putMapping.value()[0];
            }
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            requestMapping = method.getAnnotation(RequestMapping.class);
            requestMethod = requestMapping.method()[0].name();
            if (requestMapping.value().length > 0) {
                url = requestMapping.value()[0];
            }
        }
        if (StringUtils.isNotBlank(baseUrl)) {
            url = baseUrl + url;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("requestUrl", url);
        map.put("requestMethod", requestMethod);
        return map;
    }

    /**
     * 获取默认key
     *
     * @return {@link String}
     */
    public String getDefaultKey(Method method, JoinPoint pjp) {
        String requestUrl = this.getRequestUrlAndMethod(method, pjp).get("requestUrl");
        String requestMethod = this.getRequestUrlAndMethod(method, pjp).get("requestMethod");
        return method.getName() + ":" + requestMethod.toLowerCase() + ":" + requestUrl;
    }

}
