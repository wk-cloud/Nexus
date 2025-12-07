package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限状态枚举
 *
 * @author wk
 * @date 2025/12/07
 */
@AllArgsConstructor
@Getter
public enum PermissionStateEnum {

    NORMAL(1, "正常"),
    DISABLE(0, "禁用");

    private final Integer code;
    private final String info;
}
