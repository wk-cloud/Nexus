package com.nexus.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 *  登录方式枚举
 * */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    PASSWORD(1,"密码登录"),
    QQ(2,"QQ登录"),
    PASSWORD_FREE(3,"免密登录");

    private final Integer code;
    private final String info;
}
