package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电子邮件模板枚举
 *
 * @author wk
 * @date 2025/04/06
 */
@Getter
@AllArgsConstructor
public enum EmailTemplateEnum {

    VERIFICATION_CODE("【编程笔录】：安全验证码", "正在进行身份验证，请勿泄露验证码：【 %s 】\n注意：验证码有效时间为1分钟");

    private final String subject;
    private final String text;

}
