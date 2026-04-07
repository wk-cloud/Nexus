package com.nexus.common.core.utils;

import cn.hutool.crypto.digest.MD5;

import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 *
 * @author wk
 * @date 2026/4/7 10:16
 */
public class EncryptionUtils {

    /**
     * 默认哈希散列次数
     */
    private static final int DEFAULT_HASH_ITERATIONS = 1024;

    private EncryptionUtils() {}

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
        return md5Hash(password, salt, hashIterations);
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @return {@link String }
     */
    public static String md5Hash(String source) {
        return MD5.create().digestHex(source);
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @param salt   随机盐
     * @return {@link String }
     */
    public static String md5Hash(String source, String salt) {
        return MD5.create().setSalt(salt.getBytes(StandardCharsets.UTF_8)).digestHex(source);
    }

    /**
     * md5哈希
     *
     * @param source 来源
     * @param salt   随机盐
     * @return {@link String }
     */
    public static String md5Hash(String source, String salt, int hashIterations) {
        return MD5.create().setSalt(salt.getBytes(StandardCharsets.UTF_8)).setDigestCount(hashIterations).digestHex(source);
    }
}
