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

    private UserNameUtils() {
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定位数的数字和英文混合编码
     *
     * @param accountNumber 账号
     * @param begin         开始位置
     * @param end           结束位置
     * @return {@link String}
     */
    public static String generate(String accountNumber, Integer begin, Integer end) {
        String code = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        return addPrefix(accountNumber, code);
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定长度纯数字编码
     *
     * @param accountNumber 账号
     * @param len           编码长度。若为 null 则默认为 10，最小值为0
     * @return {@link String}
     */
    public static String generate(String accountNumber, Integer len) {
        return addPrefix(accountNumber, generateNumberCode(len));
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定位数的数字和英文混合编码
     *
     * @param loginType 登录类型
     * @param begin     开始位置
     * @param end       结束位置
     * @return {@link String}
     */
    public static String generate(Integer loginType, Integer begin, Integer end) {
        String code = UUID.randomUUID().toString().replaceAll("-", "").substring(begin, end);
        return addPrefix(loginType, code);
    }

    /**
     * 生成用户名
     * 组成部分：前缀 + 指定长度的纯数字编码
     *
     * @param loginType 登录类型
     * @param len       编码长度。若为 null 则默认为 10，最小值为0
     * @return {@link String}
     */
    public static String generate(Integer loginType, Integer len) {
        return addPrefix(loginType, generateNumberCode(len));
    }

    /**
     * 生成数字编码
     *
     * @param len 编码长度
     * @return {@link String }
     */
    private static String generateNumberCode(Integer len) {
        if (len == null) {
            len = 10;
        }
        len = Math.max(len, 0);
        StringBuilder code = new StringBuilder(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, len));
        // 如果code的长度不足len，则填充随机0-9
        if (code.length() < len) {
            int remainLen = len - code.length();
            for (int i = 0; i < remainLen; i++) {
                code.append((int) (Math.random() * 10));
            }
        }
        return code.toString();
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
     * @param code          编码
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
