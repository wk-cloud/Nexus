package com.nexus.common.core.rule.test.action;

import lombok.extern.slf4j.Slf4j;

/**
 * 控制台打印动作
 *
 * @author wk
 * @date 2025/09/15
 */
@Slf4j
public class Console {
    public static void print() {
        log.info("====> 函数动作调用成功");
    }
}