package com.nexus.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Redis 工具类
 *
 * @author wk
 * @date 2022/8/17
 */
@Slf4j
public class RedisUtils {

    /**
     * redisTemplate
     */
    private static final RedisTemplate<String, Object> staticRedisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);

    private RedisUtils() {
    }

    // --------------------------- 通用方法 ---------------------------

    /**
     * 设置键值对
     *
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        staticRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置键值对并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public static void setEx(String key, Object value, long timeout, TimeUnit unit) {
        staticRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public static Object get(String key) {
        return staticRedisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     *
     * @param key 键
     * @return 是否删除成功
     */
    public static Boolean delete(String key) {
        return staticRedisTemplate.delete(key);
    }

    /**
     * 删除键
     *
     * @param keys 键列表
     * @return 删除键的数量
     */
    public static Long delete(Collection keys) {
        return staticRedisTemplate.delete(keys);
    }

    /**
     * 检查键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public static Boolean hasKey(String key) {
        return staticRedisTemplate.hasKey(key);
    }

    /**
     * 设置键的过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public static Boolean expire(String key, long timeout, TimeUnit unit) {
        return staticRedisTemplate.expire(key, timeout, unit);
    }

    // --------------------------- 原子操作 ---------------------------

    /**
     * 自增操作
     *
     * @param key   键
     * @param delta 增量
     * @return 自增后的值
     */
    public static Long increment(String key, long delta) {
        return staticRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减操作
     *
     * @param key   键
     * @param delta 减量
     * @return 自减后的值
     */
    public static Long decrement(String key, long delta) {
        return staticRedisTemplate.opsForValue().decrement(key, delta);
    }

    // --------------------------- 哈希操作 ---------------------------

    /**
     * 设置哈希字段值
     *
     * @param key     哈希键
     * @param hashKey 字段名
     * @param value   字段值
     */
    public static void hPut(String key, String hashKey, Object value) {
        staticRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 获取哈希字段值
     *
     * @param key     哈希键
     * @param hashKey 字段名
     * @return 字段值
     */
    public static Object hGet(String key, String hashKey) {
        return staticRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 删除哈希字段
     *
     * @param key      哈希键
     * @param hashKeys 要删除的字段名
     * @return 删除的字段数量
     */
    public static Long hDelete(String key, String... hashKeys) {
        return staticRedisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 检查哈希字段是否存在
     *
     * @param key     哈希键
     * @param hashKey 字段名
     * @return 是否存在
     */
    public static Boolean hHasKey(String key, String hashKey) {
        return staticRedisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /**
     * 取出指定key下的hash值列表
     *
     * @param key 哈希key
     * @return {@link List }<{@link String }>
     */
    public static List<Object> hValues(String key) {
        return staticRedisTemplate.opsForHash().values(key);
    }

    /**
     * 取出指定key下的键列表
     *
     * @param key 哈希key
     * @return {@link Set }<{@link String }>
     */
    public static Set<String> hKeys(String key) {
        return ObjectUtils.toSet(staticRedisTemplate.opsForHash().keys(key), String.class);
    }

    /**
     * 原子操作
     *
     * @param key     哈希键
     * @param hashKey 字段名
     * @param delta   增量
     * @return {@link Long }
     */
    public static Long hIncrement(String key, String hashKey, Integer delta) {
        return staticRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    // --------------------------- 列表操作 ---------------------------

    /**
     * 左推入列表
     *
     * @param key   列表键
     * @param value 值
     * @return 列表长度
     */
    public static Long lPush(String key, Object value) {
        return staticRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 右推入列表
     *
     * @param key   列表键
     * @param value 值
     * @return 列表长度
     */
    public static Long rPush(String key, Object value) {
        return staticRedisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 左弹出列表元素
     *
     * @param key 列表键
     * @return 弹出的值
     */
    public static Object lPop(String key) {
        return staticRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 右弹出列表元素
     *
     * @param key 列表键
     * @return 弹出的值
     */
    public static Object rPop(String key) {
        return staticRedisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取列表范围
     *
     * @param key   列表键
     * @param start 起始索引
     * @param end   结束索引
     * @return 元素列表
     */
    public static List<Object> lRange(String key, long start, long end) {
        return staticRedisTemplate.opsForList().range(key, start, end);
    }

    // --------------------------- 集合操作 ---------------------------

    /**
     * 添加集合元素
     *
     * @param key    集合键
     * @param values 值数组
     * @return 添加成功的元素个数
     */
    public static Long sAdd(String key, Object... values) {
        return staticRedisTemplate.opsForSet().add(key, values);
    }

    /**
     * 移除集合元素
     *
     * @param key    集合键
     * @param values 值数组
     * @return 移除成功的元素个数
     */
    public static Long sRemove(String key, Object... values) {
        return staticRedisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合所有元素
     *
     * @param key 集合键
     * @return 元素集合
     */
    public static Set<Object> sMembers(String key) {
        return staticRedisTemplate.opsForSet().members(key);
    }

    /**
     * 判断集合中是否存在元素
     *
     * @param key   集合键
     * @param value 集合值
     * @return
     */
    public static boolean sIsMember(String key, Object value) {
        return staticRedisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 判断集合中是否存在元素
     *
     * @param key    集合键
     * @param values
     * @return
     */
    public static Map<Object, Boolean> sIsMember(String key, Object... values) {
        return staticRedisTemplate.opsForSet().isMember(key, values);
    }

    // --------------------------- 执行脚本 ---------------------------

    /**
     * 执行脚本
     *
     * @param script  脚本
     * @param keyList 密钥列表
     * @param args    可选参数列表
     * @return {@link Long }
     */
    public static Long execute(RedisScript<Long> script, List<String> keyList, Object... args) {
        return staticRedisTemplate.execute(script, keyList, args);
    }

    // --------------------------- HyperLogLog ---------------------------

    /**
     * 向指定HyperLogLog结构添加元素，并返回更新后的基数估计值
     *
     * @param key    目标HyperLogLog键名，不存在时会自动创建
     * @param values 要添加的元素数组（可变参数），支持任意可序列化对象类型
     * @return 添加成功的元素个数
     */
    public static Long hllAdd(String key, Object... values) {
        return staticRedisTemplate.opsForHyperLogLog().add(key, values);
    }

    /**
     * 计算多个HyperLogLog结构的并集基数估计值（适用于统计独立访客数等去重计数场景）
     *
     * @param keys 需要计算基数的一个或多个HyperLogLog键名（可变参数）
     * @return {@link Long } 所有键对应HyperLogLog的并集基数估计值，当所有键都不存在时返回0
     */
    public static Long hllSize(String... keys) {
        return staticRedisTemplate.opsForHyperLogLog().size(keys);
    }

    /**
     * 删除指定HyperLogLog结构的Redis键
     *
     * @param key  需要删除的HyperLogLog键名，对应Redis中存储基数统计数据的键
     */
    public static void hllDelete(String key) {
        staticRedisTemplate.opsForHyperLogLog().delete(key);
    }

    /**
     *将多个HyperLogLog结构合并到目标键，并返回合并后的基数估计值
     *
     * @param destination 目标键名，用于存储合并后的基数统计结果
     * @param sourceKeys  需要合并的源HyperLogLog键名（可变参数）
     * @return {@link Long }
     */
    public static Long hllUnion(String destination, String... sourceKeys) {
        return staticRedisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
    }

    // --------------------------- 分布式锁 ---------------------------

    /**
     * 尝试获取分布式锁（简单实现）
     *
     * @param key     锁键
     * @param value   锁值（建议使用唯一标识）
     * @param timeout 锁超时时间
     * @param unit    时间单位
     * @return 是否获取成功
     */
    public static Boolean tryLock(String key, Object value, long timeout, TimeUnit unit) {
        return staticRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 释放分布式锁（Lua脚本保证原子性）
     *
     * @param key   锁键
     * @param value 锁值
     * @return 是否释放成功
     */
    public static Boolean unlock(String key, Object value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        Long result = staticRedisTemplate.execute(redisScript, Collections.singletonList(key), value);
        return result != null && result == 1;
    }
}
