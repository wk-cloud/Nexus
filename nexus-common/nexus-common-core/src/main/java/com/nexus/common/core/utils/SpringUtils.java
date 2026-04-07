package com.nexus.common.core.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.ApplicationContext;

/**
 * Spring 工具
 *
 * @author wk
 * @date 2025/04/01
 */
public class SpringUtils extends SpringUtil {

    private SpringUtils(){}

    /**
     * 获取 spring 上下文
     *
     * @return {@link ApplicationContext }
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }
}
