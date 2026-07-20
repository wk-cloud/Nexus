package com.nexus.common.token.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nexus.common.core.utils.*;
import com.nexus.common.redis.utils.RedisUtils;
import com.nexus.common.token.config.properties.TokenProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * token工具类
 *
 * @author wk
 * @date 2023/04/12
 */
@Slf4j
public class TokenUtils {

    private static final TokenProperties tokenProperties = SpringUtils.getBean(TokenProperties.class);

    /**
     * 签名
     */
    private static String signature = "@wk-nexus@";

    /**
     * 过期时间(s)
     */
    private static Integer expirationTime = 60 * 60 * 24;

    static {
        String customSignature = tokenProperties.getSignature();
        if (StringUtils.isNotBlank(customSignature)) {
            signature = customSignature;
        }
        String customExpirationTime = tokenProperties.getExpiration();
        if (StringUtils.isNotBlank(customExpirationTime)) {
            expirationTime = SpELUtils.evaluateInt(customExpirationTime);
            if(expirationTime < 0) {
                expirationTime = Integer.MAX_VALUE;
            }
        }
    }

    /**
     * 登录jwt令牌密钥集
     */
    public static final String LOGIN_JWT_TOKEN_KEY_SET = "login:token:set";

    private TokenUtils() {
    }

    /**
     * 初始化过期时间
     *
     * @param expirationTime 过期时间
     * @return {@link Date}
     */
    private static Date initExpirationTime(Integer expirationTime) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, expirationTime);
        return instance.getTime();
    }

    /**
     * 创建令牌
     *
     * @param payload token的有效负载（存放用户的相关信息）
     * @return {@link String}
     */
    public static String createToken(Map<String, String> payload) {
        // 创建 builder（没有设置 header，则使用默认的 header ）
        JWTCreator.Builder builder = JWT.create();

        // 设置 payload
        payload.forEach(builder::withClaim);

        // 指定令牌过期时间
        return builder
                .withExpiresAt(initExpirationTime(expirationTime))
                .sign(Algorithm.HMAC256(signature));
    }

    /**
     * 创建令牌，并将令牌放入 redis 的 set 中
     *
     * @param payload token的有效负载（存放用户的相关信息）
     * @return {@link String}
     */
    public static String createTokenForRedisSet(Map<String, String> payload) {
        String token = createToken(payload);
        RedisUtils.sAdd(LOGIN_JWT_TOKEN_KEY_SET, token);
        return token;
    }

    /**
     * 从 redis 的 set 中删除令牌
     *
     * @param token 令牌
     */
    public static void removeTokenFromRedisSet(String token) {
        RedisUtils.sRemove(LOGIN_JWT_TOKEN_KEY_SET, token);
    }

    /**
     * 从 redis 的 set 中批量删除令牌
     *
     * @param tokens 令牌
     */
    public static void removeTokenFromRedisSet(Collection<String> tokens) {
        RedisUtils.sRemove(LOGIN_JWT_TOKEN_KEY_SET, tokens.toArray());
    }

    /**
     * 从 redis 的 set 中获取令牌集合
     *
     * @return {@link Set}<{@link String}>
     */
    public static Set<String> getTokensFromRedisSet() {
        return ObjectUtils.toSet(RedisUtils.sMembers(LOGIN_JWT_TOKEN_KEY_SET), String.class);
    }

    /**
     * 判断token是否存在
     *
     * @param token 令牌
     * @return boolean
     */
    public static boolean isExistOfRedisSet(String token) {
        return RedisUtils.sIsMember(LOGIN_JWT_TOKEN_KEY_SET, token);
    }

    /**
     * 检查令牌
     *
     * @param token 令牌
     * @return {@link DecodedJWT}
     */
    public static DecodedJWT checkToken(String token) {
        return JWT.require(Algorithm.HMAC256(signature)).build().verify(token);
    }


    /**
     * 检查是否令牌过期
     *
     * @param token 令牌
     * @return boolean
     */
    public static boolean isExpired(String token) {
        try {
            return checkToken(token).getExpiresAt().getTime() < System.currentTimeMillis();
        } catch (Exception e) {
            return true;
        }
    }


    /**
     * 获取token中的指定信息
     *
     * @param token 令牌
     * @param key   关键词
     * @return {@link String}
     */
    public static String getValueFromToken(String token, String key) {
        return checkToken(token).getClaim(key).asString();
    }


    /**
     * 获取 token 中 设置的相关信息集合
     *
     * @param token   令牌
     * @param keyList 关键词
     * @return {@link Map}<{@link String},{@link String}>
     */
    public static Map<String, String> getValueFromToken(String token, List<String> keyList) {
        if (!isExpired(token) && CollectionUtils.isNotEmpty(keyList)) {
            HashMap<String, String> resultMap =
                    new HashMap<>(CollectionUtils.initialCapacity(keyList.size()));
            keyList.forEach(key -> {
                resultMap.put(key, checkToken(token).getClaim(key).asString());
            });
            return resultMap;
        }
        return Collections.emptyMap();
    }
}
