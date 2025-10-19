package com.nexus.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * token工具类
 *
 * @author wk
 * @date 2023/04/12
 */
@Slf4j
@Component
public class TokenUtils {

    /**
     * 签名
     */
    private static String signature = "@wk-blog";

    /**
     * 过期时间
     */
    private static Integer expirationTime = 60 * 60 * 24;

    @PostConstruct
    public void init(){
        String customSignature = SpringUtils.getProperty("token.signature");
        if (StringUtils.isNotBlank(customSignature)) {
            signature = customSignature;
        }
        String customExpirationTime = SpringUtils.getProperty("token.expiration-time");
        if (StringUtils.isNotBlank(customExpirationTime)) {
            expirationTime = SpELUtils.evaluateInt(customExpirationTime);
        }
    }

    /**
     * 登录jwt令牌密钥集
     */
    public static final String LOGIN_JWT_TOKEN_KEY_SET = "login:token:set:";

    /**
     * 登录jwt令牌密钥哈希
     */
    public static final String LOGIN_JWT_TOKEN_KEY_HASH = "login:token:hash:";

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
     * @param map token的有效负载（存放用户的相关信息）
     * @return {@link String}
     */
    public static String createToken(Map<String, String> map) {
        // 创建 builder（没有设置 header，则使用默认的 header ）
        JWTCreator.Builder builder = JWT.create();

        // 设置 payload
        map.forEach(builder::withClaim);

        // 指定令牌过期时间
        return builder
                .withExpiresAt(initExpirationTime(expirationTime))
                .sign(Algorithm.HMAC256(signature));
    }

    /**
     * 创建令牌，并将令牌放入 redis 的 set 中
     *
     * @param map token的有效负载（存放用户的相关信息）
     * @return {@link String}
     */
    public static String createTokenForRedisSet(Map<String, String> map) {
        String token = createToken(map);
        RedisUtils.sAdd(LOGIN_JWT_TOKEN_KEY_SET, token);
        return token;
    }

    /**
     * 创建令牌，并将令牌放入 redis 的 hash 中
     *
     * @param tokenId 令牌id
     * @param map     token的有效负载（存放用户的相关信息）
     * @return {@link String}
     */
    public static String createTokenForRedisHash(String tokenId, Map<String, String> map) {
        String token = createToken(map);
        RedisUtils.hPut(LOGIN_JWT_TOKEN_KEY_HASH, tokenId, token);
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
     * 从 redis 的 hash 中删除令牌
     *
     * @param tokenId 令牌 ID
     */
    public static void removeTokenFromRedisHash(String tokenId) {
        RedisUtils.hDelete(LOGIN_JWT_TOKEN_KEY_HASH, tokenId);
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
     * 从 redis 的 hash 中获取令牌集合
     *
     * @return {@link List}<{@link String}>
     */
    public static List<String> getTokensFromRedisHash() {
        // 从 RedisUtils 中获取 LOGIN_JWT_TOKEN_KEY_HASH 的值
        return ObjectUtils.toList(RedisUtils.hValues(LOGIN_JWT_TOKEN_KEY_HASH), String.class);
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
     * 判断token是否存在
     *
     * @param tokenId 令牌 ID
     * @param token   令 牌
     * @return boolean
     */
    public static boolean isExistOfRedisHash(String tokenId, String token) {
        String o = (String) RedisUtils.hGet(LOGIN_JWT_TOKEN_KEY_HASH, tokenId);
        return token.equals(o);
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
     * @return {@link Object}
     */
    public static Object getValueFromToken(String token, String key) {
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
                    new HashMap<>(CollectionUtils.getInitialCapacity(keyList.size()));
            keyList.forEach(key -> {
                resultMap.put(key, checkToken(token).getClaim(key).asString());
            });
            return resultMap;
        }
        return Collections.emptyMap();
    }
}
