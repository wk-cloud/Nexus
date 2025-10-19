package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * 脱敏类型枚举
 *
 * @author wk
 * @date 2024/12/29
 */
@Getter
@AllArgsConstructor
public enum DesensitizationTypeEnum {

    // 手机号脱敏策略，保留前三位和后四位
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),

    // 邮箱脱敏策略，保留邮箱用户名第一个字符和@符号前后部分
    EMAIL(s -> s.replaceAll("(\\w)[^@]*(@\\w+\\.\\w+)", "$1****$2")),

    // 身份证号脱敏策略，保留前四位和后四位
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2")),

    // 地址脱敏策略，保留省市信息，其余部分脱敏为**
    ADDRESS(s -> s.replaceAll("([\\u4e00-\\u9fa5]{2})[\\u4e00-\\u9fa5]+", "$1**")),

    // 银行卡号脱敏策略，保留前四位和后三位
    BANK_CARD(s -> s.replaceAll("(\\d{4})\\d{8,12}(\\d{3})", "$1************$2")),

    // 姓名脱敏策略，保留姓氏第一个字符，其余部分脱敏为**
    NAME(s -> s.charAt(0) + "**"),

    // 密码脱敏策略，统一显示为******
    PASSWORD(s -> "******");

    private final Function<String, String> desensitization;
}
