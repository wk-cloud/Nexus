package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * HTTP 状态码枚举
 *
 * @author wk
 * @date 2022/7/21
 */
@Getter
@AllArgsConstructor
public enum HttpCodeEnum {

    // 响应成功状态码
    SUCCESS(200,"success"),
    // 响应失败状态码
    FAIL(300,"fail"),
    // 权限不足状态码
    NO_PERMISSION(50010,"没有访问权限"),
    // token失效状态码
    TOKEN_EXPIRED(50011,"登录令牌过期"),
    // 退出登录状态码
    LOGOUT(50012,"退出登录");

    private final Integer code;
    private final String info;

}
