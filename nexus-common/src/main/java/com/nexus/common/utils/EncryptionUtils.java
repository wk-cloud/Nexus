package com.nexus.common.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 加密工具类
 *
 * @author wk
 * @date 2022/7/22
 */
public class EncryptionUtils {

    /**
     * 默认哈希散列次数
     */
    private static final int DEFAULT_HASH_ITERATIONS = 1024;

    /**
     * 密码加密
     *
     * @param password 密码
     * @param salt     盐
     * @return {@link String}
     */
    public static String passwordEncryption(String password, String salt) {
        return md5Hash(password, salt, DEFAULT_HASH_ITERATIONS);
    }

    /**
     * 密码加密
     *
     * @param password       密码
     * @param salt           盐
     * @param hashIterations 哈希散列次数
     * @return {@link String}
     */
    public static String passwordEncryption(String password, String salt, int hashIterations) {
        SimpleHash simpleHash = new SimpleHash(password, salt, hashIterations);
        return simpleHash.toHex();
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @return {@link String }
     */
    public static String md5Hash(String source) {
        return new SimpleHash(source).toHex();
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @param salt   随机盐
     * @return {@link String }
     */
    public static String md5Hash(String source, String salt) {
        return new SimpleHash(source, salt).toHex();
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @param salt   随机盐
     * @return {@link String }
     */
    public static String md5Hash(String source, String salt, int hashIterations) {
        return new SimpleHash(source, salt, hashIterations).toHex();
    }
}
