package com.nexus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 管理员枚举
 *
 * @author wk
 * @date 2023/04/16
 */
@Getter
@AllArgsConstructor
public enum AdminEnum {

    SUPER_ADMIN("3052236335@qq.com","admin","超级管理员");

    private final String email;
    private final String label;
    private final String name;
}
