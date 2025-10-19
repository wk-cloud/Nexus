package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录平台枚举
 *
 * @author wk
 * @date 2024/06/30
 */
@Getter
@AllArgsConstructor
public enum LoginPlatformEnum {

    BACK_DESK(1, "后台管理"),
    FRONT(2, "前台门户");

    private final Integer code;
    private final String info;
}
