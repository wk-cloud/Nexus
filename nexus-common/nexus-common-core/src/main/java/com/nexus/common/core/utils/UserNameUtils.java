package com.nexus.common.core.utils;



import com.nexus.common.core.enums.LoginTypeEnum;

import java.util.UUID;

/**
 * 用户名工具
 *
 * @author wk
 * @date 2025/09/14
 */
public class UserNameUtils {

    private UserNameUtils(){}

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定位数的数字和英文编码
     * @param accountNumber 账号
     * @param begin         开始
     * @param end           结束
     * @return {@link String}
     */
    public static String generate(String accountNumber, Integer begin, Integer end) {
        String code = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        return addPrefix(accountNumber, code);
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 九位纯数字编码
     * @param accountNumber 账号
     * @return {@link String}
     */
    public static String generate(String accountNumber) {
        String code = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0,9);
        return addPrefix(accountNumber, code);
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定位数的数字和英文编码
     * @param loginType 登录类型
     * @param begin     开始
     * @param end       结束
     * @return {@link String}
     */
    public static String generate(Integer loginType,Integer begin,Integer end){
        String code = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        return addPrefix(loginType, code);
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 九位纯数字编码
     * @param loginType 登录类型
     * @return {@link String}
     */
    public static String generate(Integer loginType){
        String code = UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0,9);
        return addPrefix(loginType, code);
    }

    /**
     * 添加前缀
     *
     * @param loginType 登录类型
     * @param code      编码
     * @return {@link String }
     */
    private static String addPrefix(Integer loginType, String code) {
        String userName = null;
        if (LoginTypeEnum.PASSWORD.getCode().equals(loginType) || LoginTypeEnum.PASSWORD_FREE.getCode().equals(loginType)) {
            userName = "m0_" + code;
        } else if (LoginTypeEnum.QQ.getCode().equals(loginType)) {
            userName = "qq_" + code;
        }
        return userName;
    }

    /**
     * 添加前缀
     *
     * @param accountNumber 账号
     * @param code      编码
     * @return {@link String }
     */
    private static String addPrefix(String accountNumber, String code) {
        String userName = null;
        if (VerificationUtils.isEmail(accountNumber)) {
            userName = "m0_" + code;
        } else if (VerificationUtils.isQQ(accountNumber)) {
            userName = "qq_" + code;
        }
        return userName;
    }
}
