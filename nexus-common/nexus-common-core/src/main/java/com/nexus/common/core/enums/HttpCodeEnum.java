package com.nexus.common.core.enums;

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
    // 没有访问权限状态码
    NO_PERMISSION(50010,"没有访问权限"),
    // 登录状态已失效状态码
    UNAUTHORIZED(50011,"登录状态已失效，请重新登录");

    private final Integer code;
    private final String info;

}
