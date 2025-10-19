package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 角色枚举
 *
 * @author wk
 * @date 2024/04/09
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN("管理员", "admin"),
    USER("普通用户", "user"),
    TEST("测试用户", "test");

    private final String roleName;
    private final String roleLabel;

}
