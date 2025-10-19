package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 *  登录方式枚举
 * */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    EMAIL(1,"邮箱"),
    QQ(2,"QQ");

    private final Integer code;
    private final String info;
}
