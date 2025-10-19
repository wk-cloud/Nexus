package com.nexus.common.utils;


import com.nexus.common.enums.LoginTypeEnum;

import java.util.UUID;

/**
 * 用户名工具
 *
 * @author wk
 * @date 2025/09/14
 */
public class UserNameUtils {

    /**
     * 生成用户名
     *
     * @param accountNumber 账号
     * @param begin         开始
     * @param end           结束
     * @return {@link String}
     */
    public static String generate(String accountNumber, Integer begin, Integer end) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        String userName = null;
        if (VerificationUtils.isEmail(accountNumber)) {
            userName = "m0_" + uuid;
        } else if (VerificationUtils.isQQ(accountNumber)) {
            userName = "qq_" + uuid;
        }
        return userName;
    }

    /**
     * 生成用户名
     *
     * @param loginType 登录类型
     * @param begin     开始
     * @param end       结束
     * @return {@link String}
     */
    public static String generate(Integer loginType,Integer begin,Integer end){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        String userName = null;
        if (LoginTypeEnum.EMAIL.getCode().equals(loginType)) {
            userName = "m0_" + uuid;
        } else if (LoginTypeEnum.QQ.getCode().equals(loginType)) {
            userName = "qq_" + uuid;
        }
        return userName;
    }

    /**
     * 生成用户名
     *
     * @param loginType 登录类型
     * @return {@link String}
     */
    public static String generate(Integer loginType){
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0,9);
        String userName = null;
        if (LoginTypeEnum.EMAIL.getCode().equals(loginType)) {
            userName = "m0_" + uuid;
        } else if (LoginTypeEnum.QQ.getCode().equals(loginType)) {
            userName = "qq_" + uuid;
        }
        return userName;
    }
}
